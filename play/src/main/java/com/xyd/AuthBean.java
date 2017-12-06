package com.xyd;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 登录用户信息 Created by xu_shuwen on 2016/4/16.
 */
public class AuthBean {

  private String customID;
  /**
   * 当前登录的手机号
   */
  private String userId;
  /**
   * 用户昵称
   */
  private String userNick;
  /**
   * 登录类型 01 手机用户服务密码 02 固话 03 宽带（ADSL） 04 宽带（LAN） 05 网站注册用户 06 手机用户随机密码 07 WLAN业务 08 小灵通 11
   * 上网卡用户服务密码 16 上网卡用户随机密码......
   */
  private String loginType;

  private String provinceCode;
  private String cityCode;

  /**
   * 01:网厅 02:手厅
   */
  private String origin;

  public String getCustomID() {
    return customID;
  }

  public void setCustomID(String customID) {
    this.customID = customID;
  }

  public String getUserId() {
    return userId;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }

  public String getUserNick() {
    return userNick;
  }

  public void setUserNick(String userNick) {
    this.userNick = userNick;
  }

  /**
   * 登录类型 01 手机用户服务密码 02 固话 03 宽带（ADSL） 04 宽带（LAN） 05 网站注册用户 06 手机用户随机密码 07 WLAN业务 08 小灵通 11
   * 上网卡用户服务密码 16 上网卡用户随机密码......
   * 
   * @return
   */
  public String getLoginType() {
    return loginType;
  }

  /**
   * 认证手厅暂不支持区分手机和上网卡,所以目前将网厅上网卡转成手机处理
   * 
   * @param loginType
   */
  public void setLoginType(String loginType) {
    this.loginType = loginType;
  }

  public String getProvinceCode() {
    return provinceCode;
  }

  public void setProvinceCode(String provinceCode) {
    this.provinceCode = provinceCode;
  }

  public String getCityCode() {
    return cityCode;
  }

  public void setCityCode(String cityCode) {
    this.cityCode = cityCode;
  }

  public String getOrigin() {
    return origin;
  }

  public void setOrigin(String origin) {
    this.origin = origin;
  }

  @Override
  public String toString() {
    return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
  }
}
