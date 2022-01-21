package io.geewit.weixin.event.mp.common.model;

/**
 * 事件类型
 * @author geewit
 * @since 2022-01-21
 */
public enum Event {

    /**
     * 用户未关注时，进行关注后的事件推送
     */
    subscribe,

    /**
     * 用户已关注时的事件推送
     */
    SCAN,

    /**
     * 上报地理位置事件
     */
    LOCATION,

    /**
     * 点击菜单拉取消息时的事件推送
     */
    CLICK,

    /**
     * 点击菜单跳转链接时的事件推送
     */
    VIEW,

    /**
     * 扫码推事件的事件推送
     */
    scancode_push,

    /**
     * 扫码推事件且弹出“消息接收中”提示框的事件推送
     */
    scancode_waitmsg,

    /**
     * 弹出系统拍照发图的事件推送
     */
    pic_sysphoto,

    /**
     * 弹出拍照或者相册发图的事件推送
     */
    pic_photo_or_album,

    /**
     * 弹出微信相册发图器的事件推送
     */
    pic_weixin,

    /**
     * 弹出地理位置选择器的事件推送
     */
    location_select,

    /**
     * 点击菜单跳转小程序的事件推送
     */
    view_miniprogram,

    /**
     * 用户操作订阅通知弹窗
     */
    subscribe_msg_popup_event,

    /**
     * 用户管理订阅通知
     */
    subscribe_msg_change_event,

    /**
     * 事件推送群发结果
     */
    MASSSENDJOBFINISH,

    /**
     * 发送订阅通知
     */
    subscribe_msg_sent_event,

    /**
     * 模版消息发送任务完成后, 送达
     */
    TEMPLATESENDJOBFINISH,

    /**
     * 为服务号添加顾问, 结果将推送
     */
    guide_invite_result_event,

    /**
     * 微信用户扫顾问二维码后会触发事件推送
     */
    guide_qrcode_scan_event,

    /**
     * 为顾问分配客户触发事件推送
     */
    add_guide_buyer_relation_event,

    /**
     * 授权用户资料变更
     */
    user_info_modified,

    /**
     * 事件推送发布结果
     */
    PUBLISHJOBFINISH,

    /**
     * 资质认证成功
     */
    qualification_verify_success,

    /**
     * 资质认证失败
     */
    qualification_verify_fail,

    /**
     * 名称认证成功
     */
    naming_verify_success,

    /**
     * 名称认证失败
     */
    naming_verify_fail,

    /**
     * 年审通知
     */
    annual_renew,

    /**
     * 认证过期失效通知审通知
     */
    verify_expired,

    /**
     * 微信卡券 买单事件推送
     */
    user_pay_from_pay_cell,

    /**
     * 微信卡券 领取事件推送
     */
    user_get_card,

    /**
     * 微信卡券 审核通过事件推送
     */
    card_pass_check,

    /**
     * 微信卡券 审核不通过事件推送
     */
    card_not_pass_check,

    /**
     * 微信卡券 转赠事件推送
     */
    user_gifting_card,

    /**
     * 微信卡券 删除事件推送
     */
    user_del_card,

    /**
     * 微信卡券 核销事件推送
     */
    user_consume_card,

    /**
     * 微信卡券 进入会员卡事件推送
     */
    user_view_card,

    /**
     * 微信卡券 从卡券进入公众号会话事件推送
     */
    user_enter_session_from_card,

    /**
     * 微信卡券 会员卡内容更新事件
     */
    update_member_card,

    /**
     * 微信卡券 库存报警事件
     */
    card_sku_remind,

    /**
     * 微信卡券 券点流水详情事件
     */
    card_pay_order,

    /**
     * 微信卡券 会员卡激活事件推送
     */
    submit_membercard_user_info,

    /**
     * 微信卡券 用户购买礼品卡付款成功
     */
    giftcard_pay_done,

    /**
     * 微信卡券 用户购买礼品卡后赠送
     */
    giftcard_send_to_friend,

    /**
     * 微信卡券 用户领取礼品卡成功
     */
    giftcard_user_accept,

    /**
     * 子商户审核事件推送
     */
    card_merchant_check_result,

    /**
     * 微信门店 审核事件推送
     */
    poi_check_notify,

    /**
     * 微信门店 创建门店小程序的审核结果
     */
    apply_merchant_audit_info,

    /**
     * 微信门店 腾讯地图中创建门店的审核结果
     */
    create_map_poi_audit_info,

    /**
     * 微信门店 创建门店的审核结果
     */
    add_store_audit_info,

    /**
     * 微信门店 修改门店图片的审核结果
     */
    modify_store_audit_info,

    /**
     * 微信发票 收取授权完成事件推送
     */
    user_authorize_invoice,

    /**
     * 微信发票 统一开票接口-异步通知开票结果
     */
    cloud_invoice_invoiceresult_event,

    /**
     * 微信发票 发票状态更新事件推送
     */
    update_invoice_status,

    /**
     * 接收用户提交的抬头
     */
    submit_invoice_title,
}
