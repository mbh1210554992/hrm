package com.ntu.system.controller;

import com.monitorjbl.xlsx.StreamingReader;
import com.ntu.commom.controller.BaseController;
import com.ntu.commom.entity.PageResult;
import com.ntu.commom.entity.Result;
import com.ntu.commom.entity.ResultCode;
import com.ntu.commom.exception.CommonException;
import com.ntu.commom.utils.JwtUtils;
import com.ntu.domain.system.User;
import com.ntu.domain.system.response.ProfileResult;
import com.ntu.domain.system.response.UserResult;
import com.ntu.system.client.DepartmentFeignClient;
import com.ntu.system.service.PermissionService;
import com.ntu.system.service.UserService;
import org.apache.poi.ss.usermodel.*;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("/sys")
public class UserController extends BaseController {
    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private DepartmentFeignClient departmentFeignClient;

    /**
     * 通过excel批量上传员工
     *      文件上传
     */
    @PostMapping(value = "/user/import")
    public Result importUser(@RequestParam(name = "file")MultipartFile file) throws IOException {
        //1.根据Excel文件创建工作簿
        Workbook workbook = StreamingReader.builder().rowCacheSize(10).bufferSize(4096).open(file.getInputStream());
        //2.获取Sheet
        Sheet sheet = workbook.getSheetAt(0);

        List<User> list = new ArrayList<>();
        //3.获取每一行
        for(Row row : sheet){
            Object[] values = new Object[row.getLastCellNum()];
            for(int cellNum = 1; cellNum < row.getLastCellNum(); cellNum++){
                Cell cell = row.getCell(cellNum);
                values[cellNum] = getCellValue(cell);
            }
            User user = new User(values);


            list.add(user);
        }

        userService.saveAll(list,companyId,this.companyName);


        return new Result(ResultCode.SUCCESS);
    }

    public static Object getCellValue(Cell cell){
        //1.获取单元格的数据类型
        CellType type = cell.getCellType();
        Object value = null;
        //2.根据单元格类型获取数据
        switch(type){
            case STRING:
                value = cell.getStringCellValue();
                break;
            case BOOLEAN:
                value = cell.getBooleanCellValue();
                break;
            case NUMERIC:
                //日期格式
                if(DateUtil.isCellDateFormatted(cell)){
                    value = cell.getDateCellValue();
                }else{//数字
                    value = cell.getNumericCellValue();
                }
                break;
        }
        return value;
    }
    /**
     * 测试Feign组件
     * @return
     */
    @GetMapping("/test/{id}")
    public Result testFeign(@PathVariable("id") String id){
        return departmentFeignClient.findById(id);
    }

    /**
     * 分配角色
     * @param
     * @return
     */
    @PutMapping("/user/assignRoles")
    public Result save(@RequestBody Map<String, Object> map){
        //1.获取被分配的用户id
        String userId = (String)map.get("id");
        //2.获取角色的id列表
        List<String> roleIds = (List<String>)map.get("roleIds");
        //3.调用service完成角色分配
        userService.assignRoles(userId, roleIds);
        return new Result(ResultCode.SUCCESS);
    }


    @PostMapping(value = "/user")
    public Result addUser(@RequestBody User user){
        //1.设置企业id
        String companyId = this.companyId;
        String companyName =this.companyName;
        user.setCompanyId(companyId);
        user.setCompanyName(companyName);
        userService.add(user);
        return new Result(ResultCode.SUCCESS);
    }


    @GetMapping(value = "/user/{id}")
    public Result getUser(@PathVariable("id") String id){
        //添加roleIds(用户已经具有的角色id数组)
        Result result = new Result(ResultCode.SUCCESS);
        User user = userService.findById(id);
        UserResult userResult = new UserResult(user);
        result.setData(userResult);
        return result;
    }

    @GetMapping(value="/user")
    public Result getAllUser(@RequestParam(name = "page", defaultValue = "1") int page,
                             @RequestParam(name = "size", defaultValue = "5") int size, @RequestParam Map map){
        map.put("companyId",companyId);
        Page<User> userPage = userService.findAll(map,page,size);
        PageResult<User> pageResult = new PageResult(userPage.getTotalElements(),userPage.getContent());
        return new Result(ResultCode.SUCCESS,pageResult);
    }

    @PutMapping(value = "/user/{id}")
    public Result updateUser(@PathVariable("id")String id, @RequestBody User user){
        user.setId(id);
        userService.update(user);
        return new Result(ResultCode.SUCCESS);
    }

    @RequiresPermissions("API-USER-DELETE")
    @DeleteMapping(value = "/user/{id}", name = "API-USER-DELETE")
    public Result deleteUser(@PathVariable("id") String id){
        userService.deleteById(id);
        return new Result(ResultCode.SUCCESS);
    }

    /**
     * 通过service查询用户
     * 比较passworf
     * 生成jwt信息
     * @param
     * @param
     * @return
     */
    @PostMapping("/login")
    public Result login(@RequestBody Map<String,String> loginMap) throws CommonException {
        String mobile = loginMap.get("mobile");
        String password = loginMap.get("password");
        try{
            password = new Md5Hash(password,mobile,3).toString();
            //1.构造登陆令牌
            UsernamePasswordToken upToken = new UsernamePasswordToken(mobile,password);
            //2.获取subject
            Subject subject = SecurityUtils.getSubject();
            //3.调用login方法，进入realm完成认证
            subject.login(upToken);
            //4.获取sessionId
            String sessionId = (String) subject.getSession().getId();
            //5.构造返回结果
            return new Result(ResultCode.SUCCESS,sessionId);
        }catch (Exception e){
            throw new CommonException(ResultCode.MOBILE_PASSWORD_ERROR);
        }




//        User user = userService.findByMobile(mobile);
//        if(user == null || !password.equals(user.getPassword())){
//            return new Result(ResultCode.MOBILE_PASSWORD_ERROR);
//        }else {
//            //登陆成功
//            //api权限字符串
//            StringBuilder sb =new StringBuilder();
//
//            //获取到所有的可访问的api权限
//            for(Role role : user.getRoles()){
//                for(Permission permission : role.getPermissions()){
//                    if(permission.getType() == PermissionConstants.PY_API){
//                        sb.append(permission.getCode()).append(",");
//                    }
//                }
//            }
//            Map<String,Object> map = new HashMap<>();
//            map.put("apis",sb.toString()); //可访问api权限字符串
//            map.put("companyId",companyId);
//            map.put("companyName",parseCompanyName());
//            String token = jwtUtils.createJwt(user.getId(),user.getUsername(),map);
//            return new Result(ResultCode.SUCCESS,token);
//      }
    }

    /**
     * 用户登陆成功后，获取用户信息
     *   1.获取用户id
     *   2.根据id查询yoghurt
     *   3.构建返回值对象
     *   4.响应
     */
    @PostMapping("/profile")
    public Result profile(HttpServletRequest request) throws CommonException {
        /**
         * 从请求头信息中获取token数据
         *   1.获取请求头信息 名称=Authorization
         *   2.替换Bearer+空格
         *   3.解析token
         *   4.获取claims
         */

        /*Claims claims = (Claims) request.getAttribute("user_claims");
        String userId = claims.getId();

        User user = userService.findById(userId);
        //根据不同的用户级别获取用户权限

        ProfileResult profileResult = null;
        Map map =new HashMap();
        if("saasAdmin".equals(user.getLevel())){

            //1.saas平台管理员具有所有权限
            List<Permission> permissions = permissionService.findAll(map);
            profileResult = new ProfileResult(user, permissions);
        }else if("coAdmin".equals(user.getLevel())){

            //2.企业管理员具有所有的企业权限
            map.put("enVisible",1);
            List<Permission> permissions = permissionService.findAll(map);
            profileResult = new ProfileResult(user, permissions);
        }else{

            //3.企业用户具有当前角色的权限
            profileResult = new ProfileResult(user);
        }*/


        //获取session中的安全数据，
        Subject subject = SecurityUtils.getSubject();
        PrincipalCollection principals = subject.getPrincipals();
        ProfileResult o = (ProfileResult)principals.getPrimaryPrincipal();

        return new Result(ResultCode.SUCCESS,o);
    }

    @RequestMapping("/logout")
    public void logout(HttpServletResponse response) {
        Subject lvSubject=SecurityUtils.getSubject();
        lvSubject.logout();
        response.setStatus(302);
        //response.setStatusCode(HttpStatus.FOUND);
        //response.setHeader("location", Util.fillNullStr(mContextPath)+ mLoginPage);
    }

    @RequestMapping("/user/upload/{id}")
    public Result upload(@PathVariable("id")String userId,
                         @RequestParam(name="file") MultipartFile file) throws IOException, CommonException {
        //保存之后，获取图片url
       String imgUrl = userService.uploadImage(userId, file);
        return new Result(ResultCode.SUCCESS,imgUrl);
    }


}
