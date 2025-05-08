# 进入kafka docker容器
docker exec -it kafka /bin/bash

# kafka-topics.sh脚本怎么使用
kafka-topics.sh

# 创建topic
kafka-topics.sh --create --topic hello --bootstrap-server localhost:9092

# 查看topic
kafka-topics.sh --list --bootstrap-server localhost:9092

# 删除topic
kafka-topics.sh --delete --topic hello --bootstrap-server localhost:9092

# 显示主题详细信息
kafka-topics.sh --describe --topic hello --bootstrap-server localhost:9092

# 修改topic的分区数
kafka-topics.sh --alter --topic hello --partitions 5 --bootstrap-server localhost:9092

########################################################################################

# kafka-console-producer.sh脚本怎么使用
kafka-console-producer.sh

# 发送事件
kafka-console-producer.sh --topic hello --bootstrap-server localhost:9092

########################################################################################

# kafka-console-consumer.sh脚本怎么使用
kafka-console-consumer.sh

# 从头消费历史事件
# 通过 --from-beginning 参数读取主题中所有历史事件：
kafka-console-consumer.sh --topic hello --from-beginning --bootstrap-server localhost:9092

# 实时消费最新事件
kafka-console-consumer.sh --topic hello --bootstrap-server localhost:9092

# 手动重置偏移量
kafka-consumer-groups.sh --bootstrap-server localhost:9092 --group hello-group --topic hello-topic --reset-offsets --to-earliest --execute

# 手动重置偏移量(偏移到最新消息)
kafka-consumer-groups.sh --bootstrap-server <your-kafka-bootstrap-servers> --group <your-consumer-group> --topic <your-topic> --reset-offsets --to-latest --execute




