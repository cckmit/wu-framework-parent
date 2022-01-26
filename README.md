# wu-framework-parent

    安装
        brew  install gpg
    创建
        gpg --gen-key
    发送
        gpg --keyserver hkp://pool.sks-keyservers.net --send-keys D63ABA42B8ABC9DFCED412B7BB95A422DD6915C1
        
        host pool.sks-keyservers.net
        
        gpg --keyserver hkp://209.244.105.201 --send-keys D63ABA42B8ABC9DFCED412B7BB95A422DD6915C1

mvn clean install deploy -P oss -X

export GPG_TTY=$(tty)

### 1.0.4

     功能: 
        1.添加layer 层的概念
        2.自模块保持版本一直
        3.重构upsert 策略
        4.适配HBase 数据库操作
        5.添加记忆芯片功能(仿生)
   