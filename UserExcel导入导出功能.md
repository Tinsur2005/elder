# 用户Excel导入导出功能

本笔记完整说明本项目如何使用 **EasyExcel** 实现 `user`（用户）表的 **Excel 导入** 和 **导出**。整体思路：**导出**时，后端把用户表数据查出来，转成带表头的 Excel VO，通过 `HttpServletResponse` 以**文件流（blob）**形式返回给前端，前端触发浏览器下载；**导入**时，前端用 `el-upload` 把 Excel 文件 POST 给后端，后端用 EasyExcel 逐行解析，配合 `AnalysisEventListener` 一行一行写入数据库。

技术选型上用的是阿里 **EasyExcel（3.1.1）**，它底层依赖 Apache POI，但比原生 POI 更简单、内存占用更低。

---

## 一、准备工作：引入 Excel 依赖（pom.xml）

EasyExcel 依赖 POI，所以 `pom.xml` 里要同时引入 POI 和 EasyExcel：

```xml
<!--xls03-->
<dependency>
    <groupId>org.apache.poi</groupId>
    <artifactId>poi</artifactId>
    <version>4.1.1</version>
</dependency>
<!--xls07-->
<dependency>
    <groupId>org.apache.poi</groupId>
    <artifactId>poi-ooxml</artifactId>
    <version>4.1.1</version>
</dependency>
<dependency>
    <groupId>com.alibaba</groupId>
    <artifactId>easyexcel</artifactId>
    <version>3.1.1</version>
</dependency>
```

- `poi`：处理旧版 `.xls`（03 格式）。
- `poi-ooxml`：处理新版 `.xlsx`（07 格式）。
- `easyexcel`：基于 POI 的轻量级读写封装，本项目导入导出主要用它。

---

## 二、后端

### 1. Excel 数据载体（pojo/vo/UserExcelVO.java）

EasyExcel 通过 `@ExcelProperty(value = "列名")` 把 Java 字段和 Excel 表头对应起来。导出的**表头文字**、导入时**表头到字段的映射**都由它决定。本项目定义了一个专门的 `UserExcelVO`，和 `User` 实体字段一一对应：

```java
package cn.tinsur.elder.pojo.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.util.Date;

@Data
public class UserExcelVO {

    /**
     * 用户id
     */
    @ExcelProperty(value = "id")
    private Long id;

    /**
     * 姓名
     */
    @ExcelProperty(value = "姓名")
    private String name;

    /**
     * 密码哈希
     */
    @ExcelProperty(value = "密码哈希")
    private String password;

    /**
     * 手机号
     */
    @ExcelProperty(value = "手机号")
    private String phone;

    /**
     * 邮箱
     */
    @ExcelProperty(value = "邮箱")
    private String email;

    /**
     * 头像URL
     */
    @ExcelProperty(value = "头像URL")
    private String avatar;

    /**
     * 状态（0：停用，1：正常）
     */
    @ExcelProperty(value = "状态（0：停用，1：正常）")
    private Integer status;

    /**
     * 逻辑删除（0：未删除，1：已删除）
     */
    @ExcelProperty(value = "逻辑删除（0：未删除，1：已删除）")
    private Integer deleted;

    /**
     * 创建时间
     */
    @ExcelProperty(value = "创建时间")
    private Date createTime;

    /**
     * 更新时间
     */
    @ExcelProperty(value = "更新时间")
    private Date updateTime;

}
```

**核心说明：** `@ExcelProperty(value = "姓名")` 里的字符串就是导出时 Excel 那一列的**表头**。导出和导入用的是同一个 VO：导出时按这些表头把数据写进表格，导入时 EasyExcel 按这些表头把每列读回对应的字段。字段一律用 `@Data` 由 Lombok 生成读写方法。

> 额外留意：导出时 `id` 是带的那一列，`password` 也会原样导出（这里是完整复刻项目现状；若不想泄露密码哈希可去掉该字段）。

### 2. 导出工具类（util/ExcelUtil.java）

封装的通用导出方法：设置响应头（让浏览器识别为下载文件）、拿到输出流、用 EasyExcel 把数据写进去。它支持数组 `list` + 表头类 `clazz` + 文件名 + sheet 名：

```java
package cn.tinsur.elder.util;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;

public class ExcelUtil {
    public static <T> void exportExcel(HttpServletResponse resp, List<T> list, Class<T> clazz, String fileName) {
        exportExcel(resp, list, clazz, fileName, fileName);
    }

    /**
     * 导出excel
     * @param list      导出数据集合
     * @param fileName  文件的名称
     * @return 结果
     */
    public static <T> void exportExcel(HttpServletResponse resp, List<T> list, Class<T> clazz, String fileName, String sheetName ) {
        try {
            fileName = URLEncoder.encode(fileName, "UTF-8");
            resp.reset();
            resp.setCharacterEncoding("utf-8");
            resp.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=UTF-8");
            resp.setHeader("Content-disposition", "attachment;filename=" + fileName + ".xlsx");
            ServletOutputStream os = resp.getOutputStream();
            EasyExcel.write(os, clazz)
                    .autoCloseStream(false)
                    // 自动适配
                    .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                    // 大数值自动转换 防止失真
                    //.registerConverter(new ExcelBigNumberConvert())
                    .sheet(sheetName).doWrite(list);
        } catch (IOException e) {
            throw new RuntimeException("导出Excel异常");
        }
    }
}
```

**核心说明——逐行拆解：**
- 两个重载：4 参数版本 `exportExcel(...fileName)` 里把文件名同时当作 sheet 名再转调 5 参数版本。兜底用不到 5 参数版时，调用更简洁。
- `URLEncoder.encode(fileName, "UTF-8")`：把文件名 URL 编码，避免中文文件名在响应头里乱码（前端拿到后要 `decodeURIComponent` 解码回来）。
- `resp.setHeader("Content-disposition", "attachment;filename=" + fileName + ".xlsx")`：告诉浏览器这是**附件**而不是直接内嵌显示——浏览器据此触发下载，文件名叫 `${fileName}.xlsx`。
- `EasyExcel.write(os, clazz)`：用输出流写，表头/列结构由 `clazz`（`UserExcelVO`）通过 `@ExcelProperty` 决定。
- `.autoCloseStream(false)`：不让 EasyExcel 关掉我们自己的输出流，避免和 Spring 的流生命周期冲突。
- `.registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())`：**列宽自适应**，按内容最长单元格自动调列宽。
- `.sheet(sheetName).doWrite(list)`：创建名为 `sheetName` 的 sheet 并写入 `list` 数据。

### 3. 导入监听器（listener/UserExcelListener.java）

EasyExcel **导入是回调式的**：它逐行读取 Excel，每读到一行就回调 `invoke` 一次。这里是自定义了一个 `AnalysisEventListener<UserExcelVO>`，在 `invoke` 里把读到的 `UserExcelVO` 通过 `BeanUtils.copyProperties` 拷成 `User` 实体，再 `insert` 进数据库：

```java
package cn.tinsur.elder.listener;

import cn.tinsur.elder.mapper.UserMapper;
import cn.tinsur.elder.pojo.entity.User;
import cn.tinsur.elder.pojo.vo.UserExcelVO;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import org.springframework.beans.BeanUtils;

public class UserExcelListener extends AnalysisEventListener<UserExcelVO> {

    private UserMapper userMapper;

    public UserExcelListener(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public void invoke(UserExcelVO userExcelVO, AnalysisContext context) {
        User user = new User();
        BeanUtils.copyProperties(userExcelVO, user);
        user.setId(null);
        userMapper.insert(user);
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {

    }
}
```

**核心说明——逐行拆解：**
- 泛型是 `UserExcelVO`，告诉 EasyExcel 用哪个类来接收每一行的数据。
- 构造函数接收 `UserMapper`，因为**监听器是自定义 new 出来的，不走 Spring 容器**，所以没法 `@Autowired`，必须把 `UserMapper` 从外面传进来。
- `invoke(UserExcelVO userExcelVO, ...)`：**每解析一行**执行一次。
  - `BeanUtils.copyProperties(userExcelVO, user)`：把 VO 的同名字段拷到 `User` 实体。
  - `user.setId(null)`：把主键置空，让数据库自增重新分配，避免用 Excel 里带过来的 id 直接插入导致主键冲突。
  - `userMapper.insert(user)`：插入这条用户数据。
- `doAfterAllAnalysed(...)`：**所有行解析完毕**后回调一次，这里留空（不做额外处理）。

### 4. Service 接口（service/IUserService.java）

在 `IUserService` 上抽象两个方法：`exportExcel`（导出）和 `importExcel`（导入）。注意两者的参数类型——导出要 `HttpServletResponse`（用来写文件流），导入要 `MultipartFile`（接住上传的文件）：

```java
package cn.tinsur.elder.service;

import cn.tinsur.elder.pojo.entity.User;
import cn.tinsur.elder.pojo.query.UserQuery;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;

/**
 * <p>
 * 用户信息表 服务类
 * </p>
 *
 * @author Tinsur
 * @since 2026-08-24
 */
public interface IUserService extends IService<User> {

    IPage<User> list(UserQuery userQuery);

    void exportExcel(HttpServletResponse response);

    void importExcel(MultipartFile file);
}
```

### 5. Service 实现类（service/impl/UserServiceImpl.java）

实现类里负责把查到的 `User` 列表转成 `UserExcelVO` 列表交给 `ExcelUtil` 导出；导入则调用 EasyExcel 的 `read` 流式读取。完整代码如下：

```java
package cn.tinsur.elder.service.impl;

import cn.tinsur.elder.listener.UserExcelListener;
import cn.tinsur.elder.pojo.entity.User;
import cn.tinsur.elder.mapper.UserMapper;
import cn.tinsur.elder.pojo.query.UserQuery;
import cn.tinsur.elder.pojo.vo.UserExcelVO;
import cn.tinsur.elder.service.IUserService;
import cn.tinsur.elder.util.ExcelUtil;
import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * <p>
 * 用户信息表 服务实现类
 * </p>
 *
 * @author Tinsur
 * @since 2026-08-24
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {
    @Autowired
    private UserMapper userMapper;

    @Override
    public IPage<User> list(UserQuery userQuery) {
        IPage<User> page = new Page<>(userQuery.getPage(), userQuery.getLimit());
        LambdaQueryWrapper<User> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.like(!ObjectUtils.isEmpty(userQuery.getName()),User::getName,userQuery.getName())
                .like(!ObjectUtils.isEmpty(userQuery.getEmail()), User::getEmail, userQuery.getEmail())
                .between(!ObjectUtils.isEmpty(userQuery.getBeginCreateTime())
                        && !ObjectUtils.isEmpty(userQuery.getEndCreateTime()),
                        User::getCreateTime, userQuery.getBeginCreateTime(),
                        userQuery.getEndCreateTime())
                .orderByDesc(User::getCreateTime);
        return userMapper.selectPage(page, lambdaQueryWrapper);
    }

    @Override
    public void exportExcel(HttpServletResponse response) {
        List<User> list = userMapper.selectList(null); //写null则查出所有用户
        List<UserExcelVO> userExcelVOList = list.stream().map(user -> {
            UserExcelVO userExcelVO = new UserExcelVO();
            BeanUtils.copyProperties(user, userExcelVO);
            return userExcelVO;
        }).toList();
        ExcelUtil.exportExcel(response, userExcelVOList, UserExcelVO.class, "用户信息表");
    }

    @Override
    public void importExcel(MultipartFile file) {
        try {
            EasyExcel.read(file.getInputStream(), UserExcelVO.class, new UserExcelListener(userMapper)).sheet().doRead();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
```

**核心说明——导出（exportExcel）：**
- `userMapper.selectList(null)`：**参数传 null 表示不分条件，查出所有用户**（注释里写明了）。这在旧版 MyBatis-Plus 中 `selectList(null)` 是有歧义的，但当前版本下就是"查全表"。
- 把每个 `User` 用 `BeanUtils.copyProperties` 转成 `UserExcelVO`（同名字段自动拷贝），收集成 `List<UserExcelVO>`。
- `ExcelUtil.exportExcel(response, userExcelVOList, UserExcelVO.class, "用户信息表")`：4 参数版，文件名和 sheet 名都是"用户信息表"，最终下载为 `用户信息表.xlsx`。

**核心说明——导入（importExcel）：**
- `EasyExcel.read(输入流, UserExcelVO.class, 监听器).sheet().doRead()`：EasyExcel 的标准三要素——**输入流**（上传的 Excel）、**行数据类**（`UserExcelVO`）、**监听器**（自定义的 `UserExcelListener`）。
- `.sheet()`：默认读取第一个 sheet，`.doRead()` 开始真正逐行解析，每行触发监听器的 `invoke`。
- 捕获 `IOException`（读流可能失败）并转成运行时异常抛出。

### 6. Controller（controller/UserController.java 节选）

Controller 暴露两个入口：`GET /users/exportExcel` 触发导出（返回文件流，返回值是 `void`），`POST /users/importExcel` 接收上传的 `MultipartFile` 触发导入：

```java
//导出Excel
@GetMapping("/exportExcel")
public void exportExcel (HttpServletResponse response) {
    userService.exportExcel(response);
}

//导入Excel
@PostMapping("/importExcel")
public Result importExcel (MultipartFile file) {
    userService.importExcel(file);
    return Result.ok("导入成功");
}
```

**核心说明：**
- 导出接口**方法没有返回值（void）**，因为数据是直接以文件流写进 `HttpServletResponse` 的，不需要 `Result` 包装。
- 导入接口的形参 `MultipartFile file` 由 Spring 自动封装上传上来的文件（前端表单字段名必须是 `file`）。导入成功返回 `Result.ok("导入成功")`，前端 `ElMessage` 提示。

---

## 三、前端

### 1. axios 对 blob 响应的特殊处理（utils/request.js）

导出的接口返回的是**二进制文件流**不是 JSON。如果统一的响应拦截器还是按 JSON 处理（`return response.data`），前端就拿不到响应头和 Blob 了。所以 `request.js` 里针对 `responseType === 'blob'` 的请求**直接返回整个 `response`**，方便前端读取文件名头和解码 Blob：

```js
import axios from 'axios'
import {ElMessage} from 'element-plus'
import router from '@/router'

const baseURL = '/api'
const request = axios.create({baseURL})

import {useTokenStore} from "@/store/token.js";
//添加请求拦截器
request.interceptors.request.use(
    config => {
        //请求之前回调
        //添加Token
        const tokenStore = useTokenStore()
        //判断token是否为空
        if(tokenStore) {
            config.headers.Authorization = tokenStore.token
        }
        return config
    },
    error => {
        return Promise.reject(error) //请求失败
    }
)

//添加响应的拦截器
request.interceptors.response.use(
    response => {
        //blob 响应(如 excel 导出)需要读取响应头里的文件名,返回完整 response ，而不是解析成json
        if (response.config.responseType === 'blob') {
            return response;
        }
        //返回result
        return response.data
    },
    error => {
        //判断响应状态码,如果为401,则证明未登录,提示请登录,并跳转到登录页面
        if (error.response.status === 401) {
            ElMessage.error('登录失效，请先登录')
            router.push('/login')
        } else {
            ElMessage.error('服务异常')
        }
        return Promise.reject(error);//异步的状态转化成失败的状态
    }
)

export default request
```

**核心说明：** 关键就是那句 `if (response.config.responseType === 'blob') { return response; }`——当请求配置里声明了 `responseType: 'blob'`，拦截器**提前返回整个 `response` 对象**（含 `response.data` 这个 Blob 和 `response.headers`），否则照常 `return response.data` 解析 JSON。

### 2. 导出接口封装（api/user.js）

导出接口要拿到二进制，所以 `method: 'get'` + `responseType: 'blob'`：

```js
import request from "@/utils/request.js";

const userApi = {
    // ......其他接口省略......
    exportExcel() {
        return request({
            url: `/users/exportExcel`,
            method: 'get',
            //XMLHttpRequest 属性 responseType 是一个枚举字符串值，用于指定响应中包含的数据类型。
            //"blob": response 是一个包含二进制数据的 Blob 对象。
            responseType: 'blob'
        })
    }
}

export default  userApi
```

**核心说明：** `responseType: 'blob'` 告诉 axios 和浏览器，响应该按二进制 Blob 处理，而不是去解析 JSON。设置了这个属性后，`request.js` 的响应拦截器才会走上面那段 `blob` 分支。注释里解释了 `responseType` 的含义。

### 3. 列表页导入导出（views/User.vue）

前端在用户列表页的**头部工具栏**放「导出Excel」按钮和「导入Excel」上传组件。

**脚本部分（导出 + 导入成功回调）：**

```js
//Excel导出
const exportExcel = () => {
  ElMessageBox.confirm(
      '您确认要导出吗Excel吗？',
      '提示',
      {
        confirmButtonText: '确认',
        cancelButtonText: '取消',
        type: 'info',
        lockScroll: false //防止抖动
      }
  ).then(() => {
    userApi.exportExcel().then((response) => {
      //从响应头 Content-Disposition 解析后端返回的文件名,后端做过 URLEncoder.encode,需要解码
      const disposition = response.headers['content-disposition'];
      let fileName = '用户信息.xlsx'; //兜底名
      if (disposition) {
        fileName = decodeURIComponent(disposition.split('filename=')[1]);
      }
      //responseType 为 blob 时 result.data 本身就是 Blob,直接用即可
      //封装的Axios类里面针对responseType 设置为 blob 的响应数据直接返回全部的response，不再返回response.data，这里直接使用 response.data
      let url = window.URL.createObjectURL(response.data);
      const link = document.createElement("a"); // 创建a标签
      link.href = url;
      link.download = fileName; // 使用后端返回的文件名
      link.click();
      URL.revokeObjectURL(url);
    });
  })
}

//导入Excel成功后调用此处
const importExcelSuccess = (result) => {
  if (result.code ==1) {
    ElMessage.success(result.msg)
    loadData()
  }
}
```

**核心说明——导出（exportExcel 方法）：**
- 先弹二次确认框，确认后才发起导出。
- `userApi.exportExcel().then((response) => {...})`：因为 `request.js` 对 blob 返回了**完整 response**，所以这里拿到的是整个响应对象。
- `response.headers['content-disposition']`：从响应头里取后端下的 `Content-disposition`，解析出 `filename=` 后面的文件名。因为是 `filename=` 直接接文件名（无 `filename*`），`split('filename=')[1]` 即可取出；后端做过 URL 编码，所以用 `decodeURIComponent` 解码。
- 取不到响应头就用兜底名 `'用户信息.xlsx'`。
- `window.URL.createObjectURL(response.data)`：把 Blob 变成可访问的临时 URL。
- 创建一个隐藏的 `<a>` 标签，`href` 指向该 URL，`download` 设成文件名，`link.click()` 触发浏览器下载，最后 `URL.revokeObjectURL(url)` 释放临时 URL。

**核心说明——导入（importExcelSuccess 回调）：**
- 导入成功（`result.code == 1`）后弹成功提示并 `loadData()` 刷新列表，让新导入的用户立即显示出来。

**模板部分（头部工具栏按钮）：**

```html
<template #header>
  <div class="header">
    <el-button type="primary" @click="showAddDialog">添加</el-button>
    <el-button type="danger" @click="deleteAll">批量删除</el-button>
    <el-button type="primary" :icon="Download" @click="exportExcel">导出Excel</el-button>
    <el-upload
        :icon="Upload"
        class="inline-block"
        multiple=""
        method="post"
        action="/api/users/importExcel"
        style="display:inline-block;margin-left: 12px"
        accept=".xlsx,.xls"
        :show-file-list="false"
        :on-success="importExcelSuccess"
        :headers="{Authorization: tokenStore.token}"
        name="file">
      <el-button type="primary" :icon="Upload">导入Excel</el-button>
    </el-upload>
  </div>
</template>
```

**核心说明：**
- **导出**：普通按钮，`@click="exportExcel"` 触发上面写的下载逻辑。
- **导入**：用 `el-upload` 组件实现。
  - `action="/api/upload"` 改为 `action="/api/users/importExcel"`，即文件一选就自动 POST 到导入接口（`/api` 前缀由 Vite 代理重写掉）。
  - `name="file"`：上传字段名必须是 `file`，才能和后端 `MultipartFile file` 对上。
  - `accept=".xlsx,.xls"`：限制只能选 Excel 文件。
  - `:headers="{Authorization: tokenStore.token}"`：上传组件不走 axios 拦截器，所以像头像上传一样手动带上 Token，通过后端的登录拦截。
  - `:show-file-list="false"`：不显示已选文件列表。
  - `:on-success="importExcelSuccess"`：上传/导入成功后回调，弹出提示并刷新列表。
  - 图标 `Upload`、`Download` 来自 `@element-plus/icons-vue`。

---

## 四、整体流程小结

```
【导出】
前端 User.vue「导出Excel」按钮 → ElMessageBox 确认
   │
   ▼
userApi.exportExcel()  →  GET /users/exportExcel （responseType: 'blob'）
   │  request.js：blob 响应返回完整 response
   ▼
UserController.exportExcel(response)
   │
   ▼
UserServiceImpl.exportExcel()
   │ userMapper.selectList(null) 查全表
   │ stream → BeanUtils.copyProperties → List<UserExcelVO>
   ▼
ExcelUtil.exportExcel(response, list, UserExcelVO.class, "用户信息表")
   │ URLEncoder.encode 文件名 / 设置 Content-disposition / 拿输出流
   │ EasyExcel.write(os, UserExcelVO.class).sheet("用户信息表").doWrite(list)
   ▼
浏览器收到 .xlsx 二进制流
   │ 前端解析 content-disposition 拿文件名 → createObjectURL → <a>.click() 下载

【导入】
前端 el-upload` 选择 .xlsx/.xls 文件
   │ 自动 POST /api/users/importExcel  （name=file + Authorization 头）
   ▼
UserController.importExcel(MultipartFile file)
   │
   ▼
UserServiceImpl.importExcel(file)
   │ EasyExcel.read(file.getInputStream(), UserExcelVO.class, new UserExcelListener(userMapper))
   │          .sheet().doRead()
   ▼
UserExcelListener.invoke(每解析一行)
   │ BeanUtils.copyProperties(VO → User) / setId(null) / userMapper.insert(user)
   ▼
全部解析完 → 返回 Result.ok("导入成功")
   │
   ▼
前端 :on-success="importExcelSuccess" → ElMessage.success + loadData() 刷新列表
```