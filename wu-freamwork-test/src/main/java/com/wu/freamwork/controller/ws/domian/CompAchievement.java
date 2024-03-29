package com.wu.freamwork.controller.ws.domian;


import com.wu.framework.easy.stereotype.upsert.EasySmart;
import com.wu.framework.easy.stereotype.upsert.EasySmartField;
import lombok.Data;

/**
 * description  信用_公路建设市场企业业绩信息表
 *
 * @author Jia wei Wu
 * @date 2020/7/14 下午1:34
 */
@Data
@EasySmart(value = "rn_rcm_csmt_comp_achievement", comment = "信用_公路建设市场企业业绩信息表 ")
public class CompAchievement {


    @EasySmartField(value = "data_id", type = "VARCHAR2(200)", comment = " IDguid ")
    private String ID;
    @EasySmartField(value = "unified_social_credit_code", type = "VARCHAR2(18)", comment = " 统一社会信用代码 ")
    private String TONGYISHEHUIXYDM;
    @EasySmartField(value = "project_number", type = "VARCHAR2(255)", comment = " 项目编号 ")
    private String XIANGMUBIANHAO;
    @EasySmartField(value = "entry_name", type = "VARCHAR2(255)", comment = " 项目名称 ")
    private String XIANGMUMINGCHENG;
    @EasySmartField(value = "construction_unit", type = "VARCHAR2(200)", comment = " 建设单位 ")
    private String JIANSHEDANWEI;
    @EasySmartField(value = "lot_no", type = "VARCHAR2(100)", comment = " 标段编号 ")
    private String BIAODUANBIANHAO;
    @EasySmartField(value = "bid_section_name", type = "VARCHAR2(200)", comment = " 标段名称 ")
    private String BIAODUANMINGCHENG;
    @EasySmartField(value = "lot_type_code", type = "VARCHAR2(20)", comment = " 标段类型代码 ")
    private String BIAODUANLEIXINGDM;
    @EasySmartField(value = "project_location_code", type = "VARCHAR2(6)", comment = " 项目所在地代码 ")
    private String XIANGMUSUOZAIDDM;
    @EasySmartField(value = "project_overview", type = "VARCHAR2(4000)", comment = " 项目概述 ")
    private String XIANGMUGAISHU;
    @EasySmartField(value = "performance_reporting_type", type = "VARCHAR2(50)", comment = " 业绩填报类型 ")
    private String YEJITIANBAOLX;
    @EasySmartField(value = "contract_amount", type = "NUMBER(20)", comment = " 合同金额 ")
    private String HETONGJINE;
    @EasySmartField(value = "settlement_price", type = "NUMBER(20)", comment = " 结算价 ")
    private String JIESUANJIA;
    @EasySmartField(value = "construction_scale", type = "VARCHAR2(4000)", comment = " 建设规模 ")
    private String JIANSHEGUIMO;
    @EasySmartField(value = "main_construction_contents", type = "VARCHAR2(4000)", comment = " 主要建设内容 ")
    private String ZHUYAOJIANSHENR;
    @EasySmartField(value = "actual_commencement_date", type = "DATE", comment = " 实际开工日期 ")
    private String SHIJIKAIGONGRQ;
    @EasySmartField(value = "planned_delivery_acceptance_date", type = "DATE", comment = " 计划交工验收日期 ")
    private String JIHUAJIAOGONGYSRQ;
    @EasySmartField(value = "delivery_acceptance_date", type = "DATE", comment = " 交工验收日期 ")
    private String JIAOGONGYANSHOURQ;
    @EasySmartField(value = "completion_date", type = "DATE", comment = " 竣工日期 ")
    private String JUNGONGRIQI;
    @EasySmartField(value = "completion_quality", type = "VARCHAR2(255)", comment = " 竣工质量 ")
    private String JUNGONGZHILIANG;
    @EasySmartField(value = "project_leader_name", type = "VARCHAR2(30)", comment = " 项目负责人姓名 ")
    private String XIANGMUFUZERXM;
    @EasySmartField(value = "project_leader_id_card_num", type = "VARCHAR2(18)", comment = " 项目负责人身份证号码 ")
    private String XIANGMUFUZERSFZHM;
    @EasySmartField(value = "technical_director_name", type = "VARCHAR2(30)", comment = " 技术负责人姓名 ")
    private String JISHUFUZERXM;
    @EasySmartField(value = "technical_director_id_card_num", type = "VARCHAR2(18)", comment = " 技术负责人身份证号码 ")
    private String JISHUFUZERSFZHM;
    @EasySmartField(value = "state", type = "VARCHAR2(10)", comment = " 状态 ")
    private String ZHUANGTAI;
    @EasySmartField(value = "data_export_time", type = "DATE", comment = " 数据导出时间 ")
    private String SHUJUDAOCHUSJ;
    @EasySmartField(value = "collection_time", type = "VARCHAR2(200)", comment = " 采集时间 ")
    private String BEGINTIME;
}
