package com.yfwl.yfgp.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.yfwl.yfgp.model.User;

public interface UserMapper {

	/**
	 * 查找user表中有没有该注册过的手机号码
	 * 
	 * @param phone
	 * @return
	 */
	Integer checkoutRegisterPhone(String phone);

	/**
	 * 新增一个用户
	 * 
	 * @param user
	 * @return
	 */
	Integer insertUser(User user);

	/**
	 * 查找user表有没有相同的用户名
	 * 
	 * @param userName
	 * @return
	 */
	Integer checkoutUsername(String userName);

	/**
	 * 更新用户信息
	 * 
	 * @param user
	 * @return
	 */
	Integer updateUserInfo(User user);

	/**
	 * 校验用户名或手机号
	 * 
	 * @param loginName
	 * @return
	 */
	Integer validateLonginName(String loginName);

	/**
	 * 校验密码
	 * 
	 * @param loginName
	 * @return
	 */
	Integer validatePassword(
			@Param(value = "phone") String phone,
			@Param(value = "password") String password);

	/**
	 * 根据登录名查找user
	 * 
	 * @param loginName
	 * @return
	 */
	User selectUserByLoginName(String loginName);

	/**
	 * 根据id查找user
	 * 
	 * @param loginName
	 * @return
	 */
	User selectUserById(Integer userId);

	/**
	 * 根据环信ID查找用户名
	 * 
	 * @param easemobId
	 * @return
	 */
	User selectUsernameByeasemobId(String easemobId);

	/**
	 * 找回密码第二步更改密码
	 * 
	 * @param password
	 * @param phone
	 * @return
	 */
	Integer updatePassword(@Param(value = "password") String password,
			@Param(value = "phone") String phone);
	
	/**
	 * 普通修改密码
	 * 
	 * @param password
	 * @param phone
	 * @return
	 */
	Integer updatePasswordGeneral(@Param(value = "password") String password,
			@Param(value = "userId") Integer userId);

	/**
	 * 查看好友列表
	 * 
	 * @param loginName
	 * @return
	 */
	List<User> selectFriendList(@Param(value = "loginName") String loginName);

	/**
	 * 查找用户（模糊查询）
	 * 
	 * @param searchName
	 * @return
	 */
	List<User> selectUserByLike(@Param(value = "searchName") String searchName,
			@Param(value = "pageCount") Integer pageCount);
	
	/**
	 * 更新头像
	 * @param headImage
	 * @param userId
	 * @return
	 */
	Integer updatHeadImage(@Param(value = "headImage") String headImage,
			@Param(value = "userId") String userId);
	
	/**
	 * 通过第三方账号查询user
	 * @param thirdAccountId
	 * @return
	 */
	User selectUserByThirdID(String thirdAccountId);
	
	/**
	 * 第三方登录时新注册一个用户
	 * @param user
	 * @return
	 */
	Integer insertUserWhenDSFDL(User user);
	
	/**
	 * 第三方登录时更新用户名
	 * @param userName
	 * @param phone
	 * @return
	 */
	Integer updateUsernameWhenDSFDL(@Param(value="userName") String userName,
			@Param(value="phone") String phone);

	Integer updateUserSexInfo(User user);

	User selectUserSuper(User user);

	Integer updateUserSuper(User user);

	List<User> getVUserList(@Param(value="userId") Integer userId);
	
	Integer updateTZtype(
			@Param(value="tzType") String tzType,
			@Param(value="userId") Integer userId);
			
	
	User selectUserByUsername(String userName);
	
	//查询所有真实用户的easemobid
	List<String> selectUserEasemodId(Integer count);
	
	//统计所有真实用户的总数
	Integer getUserCount();
	
	//改版登录注册
	User getUserByUsername(
			@Param(value="loginName") String loginName,
			@Param(value="password") String password);
	User getUserByPhone(
			@Param(value="loginName") String loginName,
			@Param(value="password") String password);
	
	Integer insertPhone(
			@Param(value="phone") String phone,
			@Param(value="userId") Integer userId);
	
	List<User> selectFriendList2(String loginName);

	List<User> getAllVUserList();

	List<User> selectRandSysUseList(
			@Param(value="start") Integer start, 
			@Param(value="limit") Integer limit);

	List<User> selectRandHighSysUseList(
			@Param(value="start") Integer start, 
			@Param(value="limit") Integer limit);	
	
	List<String> getStockAttUser(String stockCode);
	
	String getSYUser();
	
	/**
	 * @Description:获取某条数据是在这个表的第几行
	 * @param userId
	 * @return 
	 * Integer
	 * @exception:
	 * @author: 周子耀
	 * @time:2016年9月28日 下午2:59:36
	 */
	public Integer getLocation(User user);
	
	/**
	 * @Description:获取表中总记录条数
	 * @param userId
	 * @return
	 * Integer
	 * @exception:
	 * @author: 周子耀
	 * @time:2016年9月28日 下午3:11:56
	 */
	public Integer getTotalCountRecode(Integer userId);
	
	/**
	 * @Description:获取表中第n到n+3条记录
	 * @param user
	 * @return
	 * List<User>
	 * @exception:
	 * @author: 周子耀
	 * @time:2016年9月28日 下午4:06:14
	 */
	public List<User> getLimitVUser(User user);
	
	List<Integer> getXNuserid(
			@Param(value="start") Integer start, 
			@Param(value="end") Integer end);
	
	
	
	public Integer insertUser2 (User user);
	public User selectUserByIdPwd(User user);
	
	public Integer getmaiId();
	
	public Integer getnextId();
	
	
	public List<User> selectAllUsers();
}
