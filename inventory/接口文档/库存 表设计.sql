drop table if exists Inventory;

drop table if exists allot;

drop table if exists allot_warehouse;

drop table if exists channels;

drop table if exists inventory_warehouse;

drop table if exists luckyBag_warehouse_goods;

drop table if exists offline_channels;

drop table if exists presellGoods;

drop table if exists user_presell;

drop table if exists warehouse;

drop table if exists warehouse_goods;

drop table if exists warehouse_sale;

drop table if exists warehouse_stock;

drop table if exists warehouse_to_warehouse;

drop table if exists 仓库商品进销存历史表（是否需要分成3张表？？？）;

/*==============================================================*/
/* Table: Inventory                                             */
/*==============================================================*/
create table Inventory
(
   inventoryId          int(11) not null comment '库存Id',
   goodsId              int(11) comment '商品Id',
   goodsSKUID           varchar(32) comment '商品SKUID',
   count                int(11) comment '总库存量',
   onLineCount          int(11) comment '线上库存总量',
   outLineCount         int(11) comment '线下库存总量',
   primary key (inventoryId)
);

alter table Inventory comment '商品总库存表';

/*==============================================================*/
/* Table: allot                                                 */
/*==============================================================*/
create table allot
(
   id                   int(11) not null,
   goodsSKUID           varchar(32),
   importWarehouseCode  varchar(32),
   count                int(11),
   sponsor              int(11),
   auditor              int(11),
   exportMan            int(11),
   synStatus               int(11),
   createTime           datetime,
   auditTime            datetime,
   exportTime           datetime,
   importTime           datetime,
   allotCode            varchar(32),
   reason               varchar(2000),
   primary key (id)
);

alter table allot comment '商品调拨表';

/*==============================================================*/
/* Table: allot_warehouse                                       */
/*==============================================================*/
create table allot_warehouse
(
   allotId              int(11) not null,
   wareHoustCode        varchar(32),
   primary key (allotId)
);

alter table allot_warehouse comment '调拨仓库关联表';

/*==============================================================*/
/* Table: channels                                              */
/*==============================================================*/
create table channels
(
   channelsId           int(11) not null comment '渠道Id',
   name                 varchar(50) comment '渠道名',
   primary key (channelsId)
);

alter table channels comment '库存渠道字典表';

/*==============================================================*/
/* Table: inventory_warehouse                                   */
/*==============================================================*/
create table inventory_warehouse
(
   id                   int(11) comment 'id',
   inventoryId          int(11) comment '库存Id',
   wareHouseId          int(11) comment '仓库Id',
   wareHouseCode        varchar(32) comment '仓库编号'
);

alter table inventory_warehouse comment '总库存与仓库关联表(一对多)';

/*==============================================================*/
/* Table: luckyBag_warehouse_goods                              */
/*==============================================================*/
create table luckyBag_warehouse_goods
(
   id                   int(11) not null comment 'id',
   luckyBagId           int(11) comment '福袋Id',
   goodsSKUID           varchar(32) comment '商品SKUId',
   wareHouseCode        int(11) comment '仓库编码',
   primary key (id)
);

alter table luckyBag_warehouse_goods comment '福袋与商品及商品仓库对应关系表（多对多）';

/*==============================================================*/
/* Table: offline_channels                                      */
/*==============================================================*/
create table offline_channels
(
   id                   int(11) comment 'id',
   inventoryId          int(11) comment '库存Id',
   channelsId           int(11) comment '渠道Id',
   count                int(11) comment '库存数',
   goodsSKUID           varchar(32)
);

alter table offline_channels comment '线下库存与渠道关联表';

/*==============================================================*/
/* Table: presellGoods                                          */
/*==============================================================*/
create table presellGoods
(
   presellId            int(11) not null comment '预售Id',
   goodsSKUID           varchar(32) comment '商品skuId',
   inventoryId          int(11) comment '库存Id',
   presellCount         int(11) comment '可预售数量',
   shipmentsDays        int(11) comment '发货天数（eg：5天内发货）',
   remainingPresellCount int(11) comment '剩余预售数量',
   synStatus               int(1) comment '状态（是否预售下架）',
   presellMoney         decimal(19) comment '预付金额',
   primary key (presellId)
);

alter table presellGoods comment '商品预售表';

/*==============================================================*/
/* Table: user_presell                                          */
/*==============================================================*/
create table user_presell
(
   id                   int(11) not null comment 'id',
   userId               int(11) comment '用户Id',
   goodsSKUID           varchar(32) comment '预售商品skuId',
   isShipments          bit(1) comment '是否发货',
   isAdvancePayment     bit(1) comment '是否预付款',
   primary key (id)
);

alter table user_presell comment '用户与预售关联表（多对多）';

/*==============================================================*/
/* Table: warehouse                                             */
/*==============================================================*/
create table warehouse
(
   wareHouseId          int(11) not null comment '仓库Id',
   wareHouseCode        varchar(32) comment '仓库编号',
   wareHouseName        varchar(32) comment '仓库名',
   wareHouseProvince    varchar(32) comment '仓库省份',
   wareHouseCity        varchar(32) comment '仓库城市',
   wareHouseAddress     varchar(32) comment '仓库详细地址',
   type                 char comment '类型（实体店、仓库）',
   contactId            char(10),
   contactName          varchar(32) comment '联系人',
   telephone            varchar(32) comment '固话',
   phone                varchar(32) comment '手机',
   primary key (wareHouseId)
);

alter table warehouse comment '仓库表';

/*==============================================================*/
/* Table: warehouse_goods                                       */
/*==============================================================*/
create table warehouse_goods
(
   id                   int(11) not null comment 'id',
   wareHouseId          int(11) comment '仓库Id',
   wareHouseCode        varchar(32) comment '仓库编号',
   goodsId              int(11) comment '商品Id',
   goodsSKUID           varchar(32) comment '商品SKUID',
   overall              int(11) comment '总库存量',
   useCount             int(11) comment '剩余可用库存量',
   freezeCount          int(11) comment '剩余冻结库存量',
   presellCount         int(11) comment '可预售数量',
   orderFreezeCount     int(11),
   allotFreezeCount     int(11),
   primary key (id)
);

alter table warehouse_goods comment '仓库与商品关联表（多对多）';

/*==============================================================*/
/* Table: warehouse_sale                                        */
/*==============================================================*/
create table warehouse_sale
(
   id                   int(11) not null comment 'id',
   warehouseCode        varchar(32) comment '仓库编码',
   goodsSKUID           varchar(32) comment '商品SKUId',
   orderNo              varchar(32) comment '订单Id',
   count                int(11) comment '出货数量',
   comment              varchar(600) comment '备注',
   createTime           datetime comment '创建时间',
   type                 int(1),
   operator             int(11),
   saleType             int(1),
   primary key (id)
);

alter table warehouse_sale comment '仓库出货历史表';

/*==============================================================*/
/* Table: warehouse_stock                                       */
/*==============================================================*/
create table warehouse_stock
(
   id                   int(11) not null comment 'id',
   wareHouseCode        varchar(32) comment '仓库编码',
   goodsSKUID           varchar(32) comment '商品SKUId',
   count                int(11) comment '进货数量',
   batch                varchar(32) comment '批次',
   comment              varchar(600) comment '备注',
   createDate           datetime comment '创建时间',
   type                 int(11),
   operator             int(11),
   primary key (id)
);

alter table warehouse_stock comment '仓库进货历史表';

/*==============================================================*/
/* Table: warehouse_to_warehouse                                */
/*==============================================================*/
create table warehouse_to_warehouse
(
   id                   int(11) not null comment 'id',
   warehouseCode        varchar(32) comment '仓库编码',
   goodsSKUID           varchar(32) comment '商品SKUId',
   count                int(11) comment '商品数量',
   targetWarehouseCode  varchar(32) comment '目标仓库编码',
   comment              varchar(600) comment '备注',
   createTime           datetime comment '创建时间',
   operator             int(11),
   primary key (id)
);

alter table warehouse_to_warehouse comment '仓库调仓历史表';

/*==============================================================*/
/* Table: 仓库商品进销存历史表（是否需要分成3张表？？？）                              */
/*==============================================================*/
create table 仓库商品进销存历史表（是否需要分成3张表？？？）
(
   商品Id                 char(10) comment '商品Id',
   仓库编号                 char(10) comment '仓库编号',
   类型（进货、销售、调仓）         char(10) comment '类型（进货、销售、调仓）',
   数量                   char(10) comment '数量',
   备注                   char(10) comment '备注',
   目标仓库Id（只调仓时使用）       char(10) comment '目标仓库Id（只调仓时使用）',
   批次（只进货时使用）           char(10) comment '批次（只进货时使用）',
   订单Id（只销售时使用）         char(10) comment '订单Id（只销售时使用）'
);

alter table 仓库商品进销存历史表（是否需要分成3张表？？？） comment '仓库商品进销存历史表（是否需要分成3张表？？？）';

alter table inventory_warehouse add constraint FK_Reference_11 foreign key (inventoryId)
      references Inventory (inventoryId) on delete restrict on update restrict;

alter table inventory_warehouse add constraint FK_Reference_7 foreign key (wareHouseId)
      references warehouse (wareHouseId) on delete restrict on update restrict;

alter table offline_channels add constraint FK_Reference_10 foreign key (channelsId)
      references channels (channelsId) on delete restrict on update restrict;

alter table offline_channels add constraint FK_Reference_15 foreign key (inventoryId)
      references Inventory (inventoryId) on delete restrict on update restrict;

alter table presellGoods add constraint FK_Reference_9 foreign key (inventoryId)
      references Inventory (inventoryId) on delete restrict on update restrict;

alter table user_presell add constraint FK_Reference_12 foreign key (goodsSKUID)
      references presellGoods (presellId) on delete restrict on update restrict;

alter table warehouse_goods add constraint FK_Reference_8 foreign key (wareHouseId)
      references warehouse (wareHouseId) on delete restrict on update restrict;
