package cn.cqs.spring.controller;

import cn.cqs.spring.bean.BaseResponse;
import cn.cqs.spring.bean.User;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api")
public class ApiController {

    static Map<String, User> users = new HashMap<>();

    @ApiOperation(value="获取用户列表", notes="")
    @RequestMapping(value={"/getUserList"}, method=RequestMethod.GET)
    public BaseResponse getUserList() {
        List<User> r = new ArrayList<User>(users.values());
        return BaseResponse.doSuccess(r);
    }

    @ApiOperation(value="登录", notes="根据User对象登录")
    @ApiImplicitParam(name = "user", value = "用户详细实体user", required = true, dataType = "User")
    @RequestMapping(value="/login", method=RequestMethod.POST)
    public BaseResponse login(@RequestBody User user) {
        String name = user.getName();
        String password = user.getPassword();
        if (users.containsKey(name)){
            User existUser = users.get(name);
            String existPassword = existUser.getPassword();
            if (existPassword.equals(password)){
                return BaseResponse.doSuccess(user);
            } else {
                return BaseResponse.doError("密码错误");
            }
        } else {
            return BaseResponse.doError("请先完成注册！");
        }
    }

    @ApiOperation(value="注册", notes="根据User对象注册")
    @ApiImplicitParam(name = "user", value = "用户详细实体user", required = true, dataType = "User")
    @RequestMapping(value="/register", method=RequestMethod.POST)
    public BaseResponse register(@RequestBody User user) {
        String name = user.getName();
        if (users.containsKey(name)){
            return BaseResponse.doError("用户名已存在");
        } else {
            users.put(user.getName(), user);
            return BaseResponse.doSuccess("注册成功");
        }
    }
    @ApiOperation(value="获取用户详细信息", notes="根据url的id来获取用户详细信息")
    @ApiImplicitParam(name = "id", value = "用户ID", required = true, dataType = "Long")
    @RequestMapping(value="/getUser/{id}", method=RequestMethod.GET)
    public BaseResponse getUser(@PathVariable Long id) {
        User user = users.get(id);
        return BaseResponse.doSuccess(user);
    }

    @ApiOperation(value="更新用户详细信息", notes="根据url的id来指定更新对象，并根据传过来的user信息来更新用户详细信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "用户ID", required = true, dataType = "Long"),
            @ApiImplicitParam(name = "user", value = "用户详细实体user", required = true, dataType = "User")
    })
    @RequestMapping(value="/getUser/{id}", method=RequestMethod.PUT)
    public BaseResponse putUser(@PathVariable Long id, @RequestBody User user) {
        User u = users.get(id);
        u.setName(user.getName());
        users.put(user.getName(), u);
        return BaseResponse.doSuccess(u);
    }

    @ApiOperation(value="删除用户", notes="根据url的id来指定删除对象")
    @ApiImplicitParam(name = "id", value = "用户ID", required = true, dataType = "Long")
    @RequestMapping(value="/getUser/{id}", method=RequestMethod.DELETE)
    public BaseResponse deleteUser(@PathVariable Long id) {
        //users.remove(id);
        return BaseResponse.doSuccess("删除成功");
    }
}
