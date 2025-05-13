# Kafka

Apache Kafka是一款由Apache软件基金会开发的**高吞吐量、分布式、可扩展的流处理平台**，主要用于构建实时数据管道和大规模数据处理系统。以下是从学习角度为你整理的核心知识框架：

---

### 一、核心概念与架构
1. **基础定义**  
   Kafka是一个**发布-订阅消息系统**，采用分布式集群架构，支持：
    - **高吞吐**：普通硬件即可支持每秒百万级消息处理
    - **持久化存储**：通过O(1)磁盘数据结构实现TB级数据稳定存储
    - **水平扩展**：通过分区（Partition）和副本（Replica）机制支持横向扩容

2. **核心组件**
    - **Broker**：集群中的服务器节点，负责数据存储与传输
    - **Topic**：消息的逻辑分类，每个Topic可划分为多个分区（Partition）以实现并行处理
    - **Producer/Consumer**：生产者发布消息到Topic，消费者订阅并消费消息
    - **Consumer Group**：消费者组实现负载均衡，同一组内消费者共享消费进度

3. **设计特点**
    - **顺序读写与零拷贝**：通过磁盘顺序追加写入（Append-only）和零拷贝技术（Zero-copy）提升性能
    - **副本容错**：每个分区的数据在多台Broker上备份，Leader节点故障时自动切换
    - **消息保留策略**：支持按时间或数据量保留消息，默认保留7天

---

### 二、典型应用场景
1. **实时数据处理**
    - 电商订单处理（如库存实时扣减、支付状态同步）
    - 金融交易流水分析（如风控实时监控）

2. **日志聚合与分析**
    - 收集多服务器日志（如Nginx访问日志、应用错误日志），集中存储至Elasticsearch或Hadoop

3. **系统解耦与异步通信**
    - 微服务架构中，解耦服务间依赖（如订单服务与通知服务的异步通信）

4. **用户行为追踪**
    - 记录用户在网站/APP的点击、搜索等行为，用于个性化推荐与用户画像构建

---

### 三、技术特性与优势
| 特性                | 说明                                                                 |
|---------------------|--------------------------------------------------------------------|
| 高吞吐量            | 单机支持每秒数十万至百万级消息处理                          |
| 低延迟              | 端到端延迟可控制在毫秒级                                       |
| 持久化存储          | 数据默认持久化到磁盘，支持TB级存储                          |
| 可扩展性            | 通过增加Broker和分区实现水平扩展                           |
| 容错性              | 副本机制保障数据不丢失，节点故障自动恢复                    |

---

### 四、学习建议
1. **入门路径**
    - **第一步**：理解核心术语（Topic/Partition/Offset/Consumer Group）
    - **第二步**：本地搭建单节点Kafka环境，练习消息生产和消费
    - **第三步**：学习Kafka Streams或与Flink/Spark集成实现流处理

2. **资源推荐**
    - **官方文档**：[kafka.apache.org](https://kafka.apache.org/documentation/)（含完整API与配置说明）
    - **实践案例**：参考网页6中电商系统的解耦设计

---

### 五、扩展知识
- **名字由来**：开发者Jay Kreps因喜爱作家Franz Kafka而命名，与功能无关
- **版本迭代**：截至2024年12月，最新稳定版本为3.8.0（注意版本差异对API的影响）

---

下一步可深入探索Kafka的**生产者/消费者API编程**或**集群部署实践**。需要具体案例分析时，可随时告知！


---

以下是关于Kafka操作的综合介绍，涵盖核心操作流程、关键配置及最佳实践：

---

### 一、核心操作流程
#### 1. **环境搭建与启动**
- **依赖安装**  
  需先安装JDK（推荐JDK 1.8+）和Zookeeper（Kafka 3.0+版本已内置Zookeeper，可无需单独安装）。
- **Kafka安装**  
  从[官网](https://kafka.apache.org/downloads)下载安装包，解压后配置`server.properties`：
  ```properties
  broker.id=0  # 唯一Broker标识
  listeners=PLAINTEXT://:9092  # 监听地址
  log.dirs=/tmp/kafka-logs  # 日志存储路径
  zookeeper.connect=localhost:2181  # Zookeeper地址
  ```
- **服务启动**  
  依次启动Zookeeper和Kafka：
  ```bash
  # 启动Zookeeper（若使用内置）
  bin/zookeeper-server-start.sh config/zookeeper.properties
  # 启动Kafka Broker
  bin/kafka-server-start.sh config/server.properties
  ```

#### 2. **Topic管理**
- **创建Topic**  
  指定分区数和副本因子：
  ```bash
  bin/kafka-topics.sh --create --topic orders --bootstrap-server localhost:9092 \
  --partitions 3 --replication-factor 1
  ```
- **查看Topic列表**
  ```bash
  bin/kafka-topics.sh --list --bootstrap-server localhost:9092
  ```
- **删除Topic**
  ```bash
  bin/kafka-topics.sh --delete --topic orders --bootstrap-server localhost:9092
  ```

#### 3. **生产者操作**
- **命令行生产消息**
  ```bash
  bin/kafka-console-producer.sh --topic orders --bootstrap-server localhost:9092
  ```
- **Java生产者示例**  
  需配置`bootstrap.servers`和序列化器：
  ```java
  Properties props = new Properties();
  props.put("bootstrap.servers", "localhost:9092");
  props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
  props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
  
  Producer<String, String> producer = new KafkaProducer<>(props);
  ProducerRecord<String, String> record = new ProducerRecord<>("orders", "order_001", "1000元");
  producer.send(record, (metadata, e) -> {
      if (e != null) System.err.println("发送失败: " + e.getMessage());
      else System.out.println("写入分区: " + metadata.partition());
  });
  producer.close();
  ```
  同步发送使用`send().get()`，异步发送通过回调处理结果。

#### 4. **消费者操作**
- **命令行消费消息**
  ```bash
  bin/kafka-console-consumer.sh --topic orders --bootstrap-server localhost:9092 --from-beginning
  ```
- **Java消费者示例**  
  配置消费者组和反序列化器：
  ```java
  Properties props = new Properties();
  props.put("bootstrap.servers", "localhost:9092");
  props.put("group.id", "payment-service");
  props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
  props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
  props.put("auto.offset.reset", "earliest");  // 从最早消息开始消费
  
  KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
  consumer.subscribe(Collections.singletonList("orders"));
  
  while (true) {
      ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
      for (ConsumerRecord<String, String> record : records) {
          System.out.printf("收到订单: key=%s, value=%s%n", record.key(), record.value());
      }
      consumer.commitAsync();  // 手动提交偏移量
  }
  ```

---

### 二、关键配置与优化
#### 1. **生产者配置**
| 参数 | 作用 | 推荐值 |  
|------|------|-------|
| `acks` | 消息确认机制 | `all`（最高可靠性） |  
| `retries` | 发送失败重试次数 | 3-5 |  
| `batch.size` | 批量发送大小 | 16384（16KB） |  
| `linger.ms` | 发送等待时间 | 20-100ms |  

**最佳实践**：异步发送+回调处理，平衡吞吐与可靠性。

#### 2. **消费者配置**
| 参数 | 作用 | 推荐值 |  
|------|------|-------|
| `enable.auto.commit` | 自动提交偏移量 | `false`（手动提交） |  
| `max.poll.records` | 单次poll最大消息数 | 500 |  
| `session.timeout.ms` | 消费者会话超时 | 10000ms |  
| `fetch.min.bytes` | 最小拉取字节数 | 1 |  

**消费模式**：
- **订阅模式**（`subscribe()`）：自动负载均衡，适合动态消费者组。
- **指定分区**（`assign()`）：精确控制，需自行处理分区变化。

---

### 三、高级操作场景
#### 1. **消费者组管理**
- **查看消费者组状态**
  ```bash
  bin/kafka-consumer-groups.sh --list --bootstrap-server localhost:9092
  bin/kafka-consumer-groups.sh --describe --group payment-service --bootstrap-server localhost:9092
  ```
- **重置偏移量**
  ```bash
  bin/kafka-consumer-groups.sh --reset-offsets --to-earliest --topic orders --execute \
  --bootstrap-server localhost:9092 --group payment-service
  ```

#### 2. **数据可靠性保障**
- **副本机制**：通过`replication.factor`设置副本数（建议≥3）。
- **ISR机制**：仅同步副本（In-Sync Replicas）参与写入确认。

#### 3. **性能调优**
- **分区数规划**：分区数=消费者组内消费者数量×2（避免资源闲置）。
- **JVM参数优化**：调整堆内存（`-Xmx4G -Xms4G`）和GC算法（G1）。

---

### 四、典型应用场景
1. **日志收集**：Flume采集日志写入Kafka，供ELK分析。
2. **实时流处理**：与Flink/Spark Streaming集成，处理交易数据。
3. **系统解耦**：微服务间通过Kafka异步通信，如订单与库存服务。

---

### 参考资料
: Kafka核心架构与概念
: 生产者与消费者操作
: 性能调优与高级配置
: 实际应用场景与案例

---

以下是关于在Kafka中创建Topic的详细指南，综合了不同场景下的操作方式和注意事项：

---

### 一、创建Topic的三种核心方式
#### 1. **命令行工具（推荐）**
使用`kafka-topics.sh`脚本创建，支持灵活配置分区和副本参数：
```bash
./kafka-topics.sh --create \
  --bootstrap-server localhost:9092 \  # Kafka Broker地址
  --topic my-topic \                  # Topic名称（需唯一）
  --partitions 3 \                   # 分区数（建议≤200）
  --replication-factor 2             # 副本数（≤Broker节点数）
```
**参数说明**：
- `--partitions`：分区数决定Topic的并行处理能力，需根据消费者数量和吞吐量需求设置
- `--replication-factor`：副本数影响数据可靠性，生产环境建议≥3
- 高版本Kafka（≥2.2）需用`--bootstrap-server`替代`--zookeeper`

#### 2. **Java API编程**
通过`AdminClient`实现动态创建，适合自动化场景：
```java
Properties props = new Properties();
props.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
AdminClient admin = AdminClient.create(props);

NewTopic newTopic = new NewTopic("my-topic", 3, (short) 2);
CreateTopicsResult result = admin.createTopics(Collections.singleton(newTopic));
result.all().get(); // 阻塞等待创建完成
admin.close();
```
**注意事项**：
- 需添加Maven依赖`kafka-clients`（版本需与Kafka服务端一致）
- 异常处理需捕获`InterruptedException`和`ExecutionException`

#### 3. **管理控制台（云服务场景）**
适用于阿里云、华为云等托管Kafka服务：
1. 登录云服务控制台，进入Kafka实例详情页
2. 导航至「Topic管理」→「创建Topic」
3. 填写名称、分区数、副本因子等参数（部分参数受实例规格限制）

---

### 二、关键配置与注意事项
#### 1. **命名规范**
- 避免特殊字符（如`#`开头可能导致监控异常）
- 区分大小写，但Kafka内部会统一转为小写
- 禁止仅用句点和下划线区分的重复名称（如`topic_1`和`topic.1`冲突）

#### 2. **参数选择策略**
| 参数               | 推荐值                  | 说明                                                                 |
|--------------------|-------------------------|--------------------------------------------------------------------|
| 分区数             | 消费者数量×2~3           | 过多分区会增加ZooKeeper负载，建议单Topic≤200               |
| 副本因子           | 生产环境≥3               | 需≤Broker节点数，确保数据高可用                           |
| 消息保留时间       | 按业务需求（默认72小时） | 通过`log.retention.hours`配置                                |
| 自动创建Topic      | 生产环境关闭             | 避免非预期Topic占用资源（配置`auto.create.topics.enable=false`） |

#### 3. **权限控制**
- 开启密文接入时，需配置ACL策略（默认用户可创建，其他用户需授权）
- 使用SASL/SSL认证的场景需在客户端配置认证信息

---

### 三、创建后的管理操作
1. **查看Topic列表**
   ```bash
   ./kafka-topics.sh --list --bootstrap-server localhost:9092
   ```
2. **查看详细信息**
   ```bash
   ./kafka-topics.sh --describe --topic my-topic --bootstrap-server localhost:9092
   ```
3. **删除Topic**
   ```bash
   ./kafka-topics.sh --delete --topic my-topic --bootstrap-server localhost:9092
   ```
   需确保服务端配置`delete.topic.enable=true`

4. **修改配置**
   ```bash
   ./kafka-configs.sh --alter --entity-type topics --entity-name my-topic \
   --add-config max.message.bytes=10485760 --bootstrap-server localhost:9092
   ```

---

### 四、常见问题解决
1. **分区数不足导致性能瓶颈**
   - 通过`kafka-topics.sh --alter`动态扩容分区
   - 需注意分区重平衡可能导致短暂消费延迟

2. **副本同步失败（ISR机制）**
   - 检查网络连通性和磁盘空间
   - 监控`UnderReplicatedPartitions`指标

3. **自动创建Topic参数不合理**
   - 修改`num.partitions`和`default.replication.factor`默认值

---

通过合理配置Topic参数并结合业务场景选择创建方式，可有效提升Kafka集群的稳定性和处理能力。建议初次使用优先通过命令行工具验证，生产环境采用API或自动化平台集成。


---

以下是使用 `kafka-console-producer.sh` 向 Kafka 主题（Topic）写入事件（Events）的完整指南，综合多个场景的操作方法与优化技巧：

---

### 一、基础操作：手动输入事件
#### 1. **启动生产者并写入事件**
通过命令行工具直接向指定主题发送文本消息：
```bash
./kafka-console-producer.sh \
  --bootstrap-server localhost:9092 \  # Kafka Broker地址
  --topic orders                       # 目标主题名称
```
- **操作流程**：
    1. 执行命令后进入交互式输入模式；
    2. 逐行输入事件内容（如 `{"order_id": 1001, "amount": 200}`），按回车发送；
    3. 按 `Ctrl+C` 退出生产者。

#### 2. **带键值对的事件写入**
通过指定键值分隔符发送结构化数据：
```bash
./kafka-console-producer.sh \
  --bootstrap-server localhost:9092 \
  --topic user_actions \
  --property "parse.key=true" \        # 启用键解析
  --property "key.separator=:"         # 键值分隔符（例如 key1:value1）
```
- **示例输入**：
  ```text
  user123:{"action": "login", "time": "2025-05-08T10:00:00"}
  user456:{"action": "purchase", "item": "book"}
  ```

---

### 二、批量导入：从文件写入事件
#### 1. **直接导入文本文件**
将文件内容逐行发送到主题：
```bash
./kafka-console-producer.sh \
  --bootstrap-server localhost:9092 \
  --topic logs \
  --file /path/to/logfile.txt          # 文件路径
```
- **文件格式要求**：每行一个独立事件（支持纯文本或 JSON 格式）。

#### 2. **自定义编码与格式**
处理特殊编码文件或指定数据格式：
```bash
./kafka-console-producer.sh \
  --bootstrap-server node1:9092 \
  --topic csv_data \
  --property "file.encoding=UTF-8" \    # 文件编码
  --property "format.type=csv"         # 数据格式（如 CSV）
```

---

### 三、高级配置：性能优化与可靠性
#### 1. **提升吞吐量**
通过批量发送和压缩减少网络开销：
```bash
./kafka-console-producer.sh \
  --bootstrap-server localhost:9092 \
  --topic high_throughput \
  --producer-property "batch.size=16384" \     # 批量大小（16KB）
  --producer-property "linger.ms=50" \         # 等待时间（毫秒）
  --producer-property "compression.type=gzip"  # 压缩算法
```

#### 2. **可靠性配置**
平衡数据一致性与性能：
```bash
./kafka-console-producer.sh \
  --bootstrap-server localhost:9092 \
  --topic financial_txns \
  --producer-property "acks=all" \       # 等待所有副本确认
  --producer-property "retries=5"       # 失败重试次数
```

---

### 四、分区控制策略
#### 1. **通过键（Key）分配分区**
相同键的事件自动分配到同一分区，保证顺序性：
```bash
./kafka-console-producer.sh \
  --bootstrap-server localhost:9092 \
  --topic partitioned_events \
  --property "parse.key=true" \
  --property "key.separator=:" 
```
- **示例**：键为用户ID（如 `user123:event_data`）。

#### 2. **手动指定分区**
精确控制事件写入的分区号：
```bash
./kafka-console-producer.sh \
  --bootstrap-server localhost:9092 \
  --topic sensor_data \
  --property "partitioner.class=org.apache.kafka.clients.producer.RoundRobinPartitioner"
```

---

### 五、验证与调试
#### 1. **实时消费验证**
启动消费者查看写入结果：
```bash
./kafka-console-consumer.sh \
  --bootstrap-server localhost:9092 \
  --topic orders \
  --from-beginning                     # 从头读取历史事件
```

#### 2. **查看主题元数据**
确认事件分布与分区状态：
```bash
./kafka-topics.sh --describe \
  --topic orders \
  --bootstrap-server localhost:9092    # 显示分区与副本信息
```

---

### 注意事项
1. **版本兼容性**：
    - Kafka 2.2+ 使用 `--bootstrap-server` 参数，旧版本需替换为 `--broker-list`；
    - 若使用 Zookeeper 地址（旧版），需替换为 `--zookeeper localhost:2181`。

2. **文件编码问题**：非 UTF-8 文件需指定 `--property file.encoding=GBK` 等参数。

3. **生产环境建议**：避免直接暴露 Broker IP，建议通过 SASL/SSL 加密传输。

---

通过上述方法，可灵活适应不同场景的事件写入需求。若需集成代码实现自动化生产，可参考 Spring Boot 的 `KafkaTemplate` 示例。

---




以下是 Kafka 核心概念的详细解析：

![img_01](./assets/img.png)

---

### 一、生产者（Producer）
**定义**：生产者是向 Kafka 主题发送消息的客户端程序，负责将数据发布到 Kafka 集群中。  
**核心功能**：
1. **消息发送**：支持同步、异步和发送并忘记（Fire-and-Forget）三种模式，通过 `send()` 方法发送消息。
2. **分区选择**：
    - 显式指定分区（如 `new ProducerRecord("topic", 0, key, value)`）。
    - 基于 Key 哈希（相同 Key 的消息分配到同一分区，保证顺序性）。
    - 轮询策略（默认未指定 Key 时使用）。
3. **可靠性保障**：
    - `acks` 参数控制确认机制：`0`（不等待）、`1`（仅 Leader 确认）、`all`（所有副本确认）。
    - `retries` 和 `batch.size` 优化吞吐量与容错性。

---

### 二、消费者（Consumer）
**定义**：消费者是从 Kafka 主题读取消息的客户端程序，通过订阅主题并按顺序处理消息。  
**核心机制**：
1. **消费者组**：
    - 同一消费者组内的消费者共享主题订阅，每个分区仅由一个消费者处理，实现负载均衡。
    - 若消费者数量超过分区数，部分消费者会闲置。
2. **偏移量管理**：
    - 消费者通过记录分区的 `Offset`（偏移量）跟踪消费进度，支持自动提交或手动提交。
    - 消费者故障时，通过 `auto.offset.reset` 配置重置偏移量（如 `earliest` 或 `latest`）。
3. **消息拉取**：
    - 使用 `poll()` 方法批量拉取消息，参数 `max.poll.records` 控制单次拉取量。

---

### 三、主题（Topic）
**定义**：主题是消息的逻辑分类单元，用于区分不同类型的数据流（如订单、日志等）。  
**特点**：
1. **分区设计**：
    - 每个主题可划分为多个分区（Partition），分区是物理存储单元，消息按顺序追加到分区日志中。
    - 分区数决定并行处理能力，建议与消费者数量匹配（如消费者数×2）。
2. **副本机制**：
    - 每个分区有多个副本（Replica），Leader 负责读写，Follower 异步同步数据。
    - 副本因子（Replication Factor）保障数据高可用性（生产环境建议≥3）。

---

### 四、分区（Partition）
**定义**：分区是主题的物理分片，每个分区在磁盘上表现为一个有序的日志文件。  
**核心作用**：
1. **扩展性**：分区分布在多个 Broker 上，支持横向扩展存储和处理能力。
2. **顺序性保障**：
    - 同一分区内的消息按写入顺序存储，消费者按顺序读取。
    - 全局顺序性需将分区数设为 1（但会牺牲并行性）。
3. **Segment 机制**：
    - 分区被切分为多个 Segment 文件（`.log` 存储数据，`.index` 存储稀疏索引），避免单个文件过大。

---

### 五、偏移量（Offset）
**定义**：偏移量是分区内每条消息的唯一标识符，从 0 开始递增。  
**功能与分类**：
1. **生产者偏移量（LEO）**：
    - 表示生产者写入的最新消息位置（Log End Offset）。
2. **消费者偏移量**：
    - 记录消费者已处理的最新消息位置，存储在内部主题 `__consumer_offsets` 中。
    - 支持手动重置以实现消息回溯或跳过特定数据。
3. **容错与幂等性**：
    - 消费者故障后，通过偏移量恢复消费位置，避免重复处理或遗漏消息。

---

### 总结与关系
- **生产者 → 主题**：生产者将消息按策略分发到主题的分区中。
- **主题 → 分区**：通过分区实现数据分片与并行处理。
- **消费者 → 分区**：消费者组通过分区分配策略实现负载均衡。
- **偏移量 → 可靠性**：通过管理偏移量保障消息顺序性和消费进度。

---

以上概念共同构成 Kafka 高吞吐、分布式消息系统的核心架构，适用于日志收集、实时流处理等场景。

---

默认情况下，当启动一个新的消费者组时，它会从每个分区的最新偏移量（即该分区中最后一条消息的下一个位置）开始消费。如果希望从第一条消息开始消费，需要将消费者的auto.offset.reset设置为earliest；

注意： 如果之前已经用相同的消费者组ID消费过该主题，并且Kafka已经保存了该消费者组的偏移量，那么即使你设置了auto.offset.reset=earliest，该设置也不会生效，因为Kafka只会在找不到偏移量时使用这个配置。在这种情况下，你需要手动重置偏移量或使用一个新的消费者组ID；


---


# 消息消费时偏移量策略的配置

```yml
spring:
  kafka:
    consumer:
      auto-offset-reset: earliest
```
取值：`earliest`、`latest`、`none`、`exception`
- earliest：自动将偏移量重置为最早的偏移量；
- latest：自动将偏移量重置为最新偏移量；
- none：如果没有为消费者组找到以前的偏移量，则向消费者抛出异常；
- exception：向消费者抛出异常；（spring-kafka不支持）

---

```java
package com.fire.kafkaeasystudy.producer;

import jakarta.annotation.Resource;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.header.internals.RecordHeaders;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.support.SendResult;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;

@Component
public class EventProducer {

    // 加入了spring-kafka依赖 + .yml配置信息，springboot自动配置好了kafka，自动装配好了KafkaTemplate这个Bean
    @Resource
    private KafkaTemplate<String, String> kafkaTemplate;

    public void sendEvent() {
        kafkaTemplate.send("hello-topic","hello kafka");
    }

    public void sendEvent2(){
        // 通过构建器模式创建Message对象
        Message<String> message = MessageBuilder.withPayload("hello kafka message").setHeader(KafkaHeaders.TOPIC,"test-topic") // 在header中设置topic的名字
                .build();
        kafkaTemplate.send(message);
    }

    public void sendEvent3(){
        // Headers里面是放一些信息(信息是key-value键值对)，到时候消费者接收到该消息后，可以拿到这个Headers里面放的信息
        Headers headers = new RecordHeaders();
        headers.add("phone","133123214".getBytes(StandardCharsets.UTF_8));
        headers.add("orderId","OD123123412441".getBytes(StandardCharsets.UTF_8));
//        public ProducerRecord(String topic, Integer partition, Long timestamp, K key, V value, Iterable< Header > headers) {
        ProducerRecord<String, String> record = new ProducerRecord<>("test-topic-02",0,System.currentTimeMillis(),"k1","hello kafka",headers);
        kafkaTemplate.send(record);
    }


    public void sendEvent4(){
//        CompletableFuture<SendResult<K, V>> send(String topic, Integer partition, Long timestamp, K key, V data);
        kafkaTemplate.send("test-topic-02",0,System.currentTimeMillis(),"k2","hello kafka");

    }

    public void sendEvent5(){
//        CompletableFuture<SendResult<K, V>> sendDefault(Integer partition, Long timestamp, K key, V data);
//            # 配置模版默认的主题topic名称
//            template:
//                default-topic: default-topic
        kafkaTemplate.sendDefault(0,System.currentTimeMillis(),"k3","hello kafka");
    }


}

```

这段代码展示了使用 Spring Kafka 的 `KafkaTemplate` 发送消息到 Kafka 的多种方法。以下是对各方法的分析及使用注意事项的总结：

---

### **1. 基本发送方法 `sendEvent()`**
- **代码**：`kafkaTemplate.send("hello-topic", "hello kafka");`
- **作用**：向指定主题发送消息，无键、分区或 header。
- **场景**：快速发送简单消息，无需额外配置。

---

### **2. 使用 MessageBuilder `sendEvent2()`**
- **代码**：通过 `MessageBuilder` 构建消息，并在 header 中设置主题。
- **特点**：
    - **Header 指定主题**：消息的主题通过 `KafkaHeaders.TOPIC` 设置，覆盖 `send()` 方法参数（如果存在）。
    - **灵活性**：适合动态主题或需要在消息中携带元数据的场景。
- **注意**：确保 header 中的主题有效。

---

### **3. 使用 ProducerRecord 和 Headers `sendEvent3()`**
- **代码**：构造 `ProducerRecord`，包含自定义 headers、分区、时间戳、键值。
- **关键参数**：
    - **Headers**：添加业务相关元数据（如订单 ID），消费者可通过 `ConsumerRecord.headers()` 获取。
    - **指定分区**：强制消息发送到分区 0，可能引发分区不均（慎用）。
- **场景**：需携带额外信息或精确控制分区/时间戳。

---

### **4. 带分区和时间戳的发送 `sendEvent4()`**
- **代码**：`kafkaTemplate.send("test-topic-02", 0, ...)`
- **行为**：直接指定分区和时间戳，覆盖键的分区计算。
- **注意**：
    - **分区优先级**：显式指定分区时，键的分区路由失效。
    - **适用场景**：确保消息进入特定分区（如顺序性要求）。

---

### **5. 发送到默认主题 `sendEvent5()`**
- **代码**：`kafkaTemplate.sendDefault(...)`
- **前提**：配置 `spring.kafka.template.default-topic`，否则抛出异常。
- **优点**：代码简洁，适合多数消息发送到同一主题。

---

### **关键注意事项**
#### **Headers 的使用**
- **消费者读取**：通过 `@Header(value = "phone") String phone` 或 `ConsumerRecord.headers()` 获取。
- **编码**：Header 值为字节数组，需统一字符编码（如 `StandardCharsets.UTF_8`）。

#### **分区策略**
- **显式分区**：覆盖键的分区路由，可能导致负载不均。若无特殊需求，应避免硬编码分区。
- **键的作用**：未指定分区时，Kafka 根据键哈希选择分区，确保相同键进入同一分区。

#### **时间戳**
- **用途**：日志留存策略、消费者时间窗口处理等。由 Kafka 或应用自定义使用。

#### **异常处理**
- **异步结果**：`send()` 返回 `CompletableFuture`，建议处理异常：
  ```java
  kafkaTemplate.send(...).whenComplete((result, ex) -> {
      if (ex != null) {
          // 处理发送失败
      }
  });
  ```
- **同步发送**：调用 `future.get()` 阻塞等待结果（但影响性能）。

---

### **总结**
- **选择方法**：根据是否需要动态主题、元数据、分区控制等选择合适方法。
- **生产建议**：
    - 避免硬编码分区，优先依赖键的分区路由。
    - 处理发送结果，确保可靠性。
    - 合理使用 headers 传递元数据，而非业务数据。
- **配置**：确保默认主题已配置（若使用 `sendDefault`）。

正确使用这些方法可以灵活高效地集成 Kafka 到 Spring 应用中。


---

# kafkaTemplate.send(...) 和 kafkaTemplate.sendDefault(...) 的区别？

主要区别是发送消息到Kafka时是否每次都需要指定主题topic；

1. kafkaTemplate.send(...) 该方法需要明确地指定要发送消息的目标主题topic；
2. kafkaTemplate.sendDefault() 该方法不需要指定要发送消息的目标主题topic，

- kafkaTemplate.send(...) 方法适用于需要根据业务逻辑或外部输入动态确定消息目标topic的场景;
- kafkaTemplate.sendDefault() 方法适用于总是需要将消息发送到特定默认topic的场景；
- kafkaTemplate.sendDefault() 是一个便捷方法，它使用配置中指定的默认主题topic来发送消息，如果应用中所有消息都发送到同一个主题时采用该方法非常方便，可以减少代码的重复或满足特定的业务需求；


---

# 获取生产者消息发送结果
- .send()方法和.sendDefault()方法都返回CompletableFuture<SendResult<K, V>>；
- CompletableFuture 是Java 8中引入的一个类，用于异步编程，它表示一个异步计算的结果，这个特性使得调用者不必等待操作完成就能继续执行其他任务，从而提高了应用程序的响应速度和吞吐量；


- 因为调用 kafkaTemplate.send() 方法发送消息时，Kafka可能需要一些时间来处理该消息（例如：网络延迟、消息序列化、Kafka集群的负载等），如果 send() 方法是同步的，那么发送消息可能会阻塞调用线程，直到消息发送成功或发生错误，这会导致应用程序的性能下降，尤其是在高并发场景下；
- 使用 CompletableFuture.send() 方法可以立即返回一个表示异步操作结果的未来对象，而不是等待操作完成，这样，调用线程可以继续执行其他任务，而不必等待消息发送完成。当消息发送完成时（无论是成功还是失败），CompletableFuture会相应地更新其状态，并允许我们通过回调、阻塞等方式来获取操作结果；

- 方式一：调用CompletableFuture的get()方法，同步阻塞等待发送结果；
- 方式二：使用 thenAccept(), thenApply(), thenRun() 等方法来注册回调函数，回调函数将在 CompletableFuture 完成时被执行；

---


```java
public void sendEvent8(){
        User user  = User.builder().id(1208).phone("1334124124").birthday(new Date()).build();
        // 分区是null，让kafka自己去决定把消息发到哪个分区

//        producer:
//            value-serializer: org.springframework.kafka.support.serializer.ToStringSerializer
//            value-serializer: org.springframework.kafka.support.serializer.JsonSerializer

//        <dependency>
//            <groupId>com.fasterxml.jackson.core</groupId>
//            <artifactId>jackson-databind</artifactId>
//            <version>2.15.2</version>
//        </dependency>


        kafkaTemplate2.sendDefault(null,System.currentTimeMillis(),"k4",user);
    }
```

针对你的代码和配置，以下是逐步的解决方案：

1. **配置Kafka Producer使用JsonSerializer：**
   在`application.properties`或`application.yml`中设置value-serializer为JsonSerializer，并信任相关包。

   ```properties
   spring.kafka.producer.value-serializer=org.springframework.kafka.support.serializer.JsonSerializer
   spring.kafka.producer.properties.spring.json.trusted.packages=com.yourpackage.model  # 替换为User类的包名
   ```

2. **确保User类可被序列化：**
    - 为User类添加无参构造函数（如果使用Lombok的@Builder，需额外添加@NoArgsConstructor和@AllArgsConstructor）。
    - 示例User类：

      ```java
      import lombok.AllArgsConstructor;
      import lombok.Builder;
      import lombok.NoArgsConstructor;
      import java.util.Date;
 
      @Builder
      @NoArgsConstructor
      @AllArgsConstructor
      public class User {
          private int id;
          private String phone;
          private Date birthday;
          // 省略getter/setter（如果使用Lombok @Data则不需要）
      }
      ```

3. **检查KafkaTemplate配置：**
    - 确认`kafkaTemplate2`已正确配置JsonSerializer，例如：

      ```java
      @Bean
      public KafkaTemplate<String, User> kafkaTemplate2(ProducerFactory<String, User> producerFactory) {
          return new KafkaTemplate<>(producerFactory);
      }
      ```

4. **调整sendDefault方法调用：**
    - 确认参数顺序正确。`sendDefault`可能有不同的重载方法，确保传递的参数匹配方法签名。如果方法需要key和value，应调用：

      ```java
      kafkaTemplate2.sendDefault("k4", user);
      ```

    - 如果需要指定时间戳或分区，使用`send`方法并指定Topic：

      ```java
      kafkaTemplate2.send("your-topic-name", null, System.currentTimeMillis(), "k4", user);
      ```

5. **验证依赖配置：**
    - 确保`pom.xml`中包含必要的Jackson依赖：

      ```xml
      <dependency>
          <groupId>com.fasterxml.jackson.core</groupId>
          <artifactId>jackson-databind</artifactId>
          <version>2.15.2</version>
      </dependency>
      ```

6. **处理日期序列化（可选）：**
    - 如果`Date`类型序列化格式有问题，可以在User类中添加Jackson注解：

      ```java
      import com.fasterxml.jackson.annotation.JsonFormat;
 
      @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
      private Date birthday;
      ```

**最终修正后的代码示例：**

```java
public void sendEvent8() {
    User user = User.builder()
                    .id(1208)
                    .phone("1334124124")
                    .birthday(new Date())
                    .build();
    
    // 使用正确的send方法，假设sendDefault接受key和value
    kafkaTemplate2.sendDefault("k4", user);
    
    // 或明确指定分区、时间戳等
    // kafkaTemplate2.send("your-topic", null, System.currentTimeMillis(), "k4", user);
}
```

**关键点总结：**
- 确保Producer配置使用`JsonSerializer`并信任User类所在包。
- User类需要无参构造器和适当的注解以便Jackson序列化。
- 检查`sendDefault`方法参数是否正确，避免传递多余的参数导致意外行为。
- 验证依赖和KafkaTemplate配置，确保序列化器正确应用。

---

Kafka的**Replica（副本）**是其实现高可用性、容错性和数据一致性的核心机制。以下是其核心概念及工作机制的详细解析：

![img_01](./assets/img_1.png)

---

### 一、Replica的定义与作用
1. **基本定义**  
   Replica是Kafka中分区的数据副本，每个分区的数据会被复制到多个Broker上，形成多个副本。例如，若分区配置为3副本（Replication Factor=3），则数据会分布在3个不同的Broker上。

2. **核心作用**
    - **数据冗余**：通过多副本存储，防止单点故障导致数据丢失。
    - **高可用性**：当Leader副本所在的Broker宕机时，Follower副本可接替成为新Leader，保障服务连续性。
    - **负载均衡**：从Kafka 2.4版本开始，Follower副本支持有限度的读请求，分担Leader负载。

---

### 二、Replica的角色与分工
1. **Leader副本**
    - 唯一处理生产者和消费者的读写请求。
    - 负责将数据同步给Follower副本，并维护**ISR（In-Sync Replicas，同步副本集）**。

2. **Follower副本**
    - 通过异步拉取（Pull）方式从Leader同步数据，不直接处理客户端请求（Kafka 2.4前）。
    - 在Leader宕机时，通过选举机制成为新Leader。
    - **Kafka 2.4+**：允许配置Follower副本提供读服务，但需保证数据一致性（如完全同步）。

---

### 三、数据同步机制
1. **ISR（同步副本集）**
    - ISR包含所有与Leader保持同步的副本（包括Leader自身）。
    - Follower需在`replica.lag.time.max.ms`（默认10秒）内追上Leader进度，否则被移入**OSR（Out-of-Sync Replicas）**列表。

2. **同步流程**
    - 生产者发送消息至Leader，Leader写入本地日志后同步给ISR中的Follower。
    - Follower确认写入后，Leader更新**HW（High Watermark，高水位）**，表示已提交的消息偏移量。

3. **最小同步副本数（min.insync.replicas）**
    - 设置最少需同步的副本数（如2），若ISR数量不足，Leader拒绝写入，防止数据丢失。

---

### 四、数据一致性保障
1. **高水位机制（HWM）**
    - 标记所有副本已确认写入的位置，消费者仅能读取HWM之前的消息。
    - 缺点：Leader频繁切换时可能导致数据不一致。

2. **Leader Epoch机制**
    - 为每次Leader切换分配唯一Epoch标识，解决HWM机制在Leader变更时的漏洞。
    - Follower通过Epoch判断数据是否与Leader一致，确保恢复过程精准。

---

### 五、副本配置与容错
1. **关键参数**
    - `replication.factor`：副本数（生产环境建议≥3）。
    - `unclean.leader.election.enable`：是否允许非ISR副本（OSR）参与选举。
        - `false`（默认）：禁止，避免数据丢失但可能牺牲可用性。
        - `true`：允许，提升可用性但可能引发数据不一致。

2. **故障转移流程**
    - Leader宕机后，优先从ISR中选举新Leader；若ISR为空，根据`unclean.leader.election.enable`决定是否启用OSR副本。

---

### 六、Replica的局限性
- **读写分离限制**：默认仅Leader处理读写请求，Follower仅同步数据（2.4版本前）。
- **资源开销**：高副本数会增加存储和网络负载，需权衡可靠性与性能。

---

### 总结
Kafka的Replica机制通过多副本冗余、ISR动态同步、Leader选举等设计，在数据可靠性与服务可用性之间实现了平衡。合理配置副本数、ISR参数及容错策略，是优化Kafka集群性能的关键。


---

# 指定topic的分区和副本
- 方式一：通过Kafka提供的命令行工具在创建topic时指定分区和副本；

```bash
kafka-topics.sh --create --topic myTopic --partitions 3 --replication-factor 1 --bootstrap-server localhost:9092
```

- 方式二：执行代码时指定分区和副本；

```java
kafkaTemplate.send("topic", message);
```
直接使用send()方法发送消息时，kafka会帮我们自动完成topic的创建工作，但这种情况下创建的topic默认只有一个分区，分区有1个副本，也就是有它自己本身的副本，没有额外的副本备份；
我们可以在项目中新建一个配置类专门用来初始化topic；

```java
@Configuration
public class KafkaConfig {
    @Bean
    public NewTopic topic() {
        return new NewTopic("firetopic", 9, (short) 1);
    }
}
```



# 生产者发送消息的分区策略

![img_02](./assets/img_2.png)

## 轮询分配策略：RoundRobinPartitioner （接口：Partitioner）


![img_03](./assets/img_3.png)


```java
package com.fire.kafkaeasystudy.config;

import com.fasterxml.jackson.databind.ser.std.StringSerializer;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.RoundRobinPartitioner;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.validation.annotation.Validated;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConfig {
    @Bean
    public NewTopic topic() {
        return new NewTopic("firetopic", 9, (short) 1);
    }

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${spring.kafka.producer.value-serializer}")
    private String valueSerializer;

    @Value("${spring.kafka.producer.key-serializer}")
    private String keySerializer;


    /**
     * 生产者相关配置
     * @return
     */
    public Map<String, Object> producerConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,bootstrapServers);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, valueSerializer);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, keySerializer);
        props.put(ProducerConfig.PARTITIONER_CLASS_CONFIG, RoundRobinPartitioner.class);
        return props;
    }

    /**
     * 生产者创建工厂
     * @return
     */
    public ProducerFactory<String, Object> producerFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfigs());
    }

    /**
     * KafkaTemplate 覆盖默认配置中的kafkaTemplate
     * @return
     */
    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
}

```

```java
 /**
     * 看config.KafkaConfig，使用了我们自定义的分区策略，轮询分配策略
     * config.KafkaConfig:
     * props.put(ProducerConfig.PARTITIONER_CLASS_CONFIG, RoundRobinPartitioner.class);
     */
    public void sendEvent9(){
        User user  = User.builder().id(1208).phone("1334124124").birthday(new Date()).build();
        kafkaTemplate2.send("firetopic",user);
    }
```


---

## 注意

- kafkaTemplate2：被配置类的 kafkaTemplate 覆盖，使用了自定义的分区器 。
- kafkaTemplate：未被覆盖，由 Spring Boot 自动配置生成，使用默认参数。

```java
@Component
public class EventProducer {

    // 加入了spring-kafka依赖 + .yml配置信息，springboot自动配置好了kafka，自动装配好了KafkaTemplate这个Bean
    @Resource
    private KafkaTemplate<String, String> kafkaTemplate;

    @Resource
    private KafkaTemplate<String, Object> kafkaTemplate2;
}
```
根据你的配置文件和代码分析，**`kafkaTemplate2` 会被配置类的 `kafkaTemplate` 覆盖，而 `kafkaTemplate` 可能未被覆盖**，具体原因如下：

---

### 一、**`kafkaTemplate2` 的覆盖逻辑**
1. **Bean 名称与类型匹配**  
   你的配置类中定义了一个 `@Bean` 方法 `kafkaTemplate()`，返回类型是 `KafkaTemplate<String, Object>`，且未指定 Bean 名称。根据 Spring 默认规则，Bean 的名称由方法名决定（即 `kafkaTemplate`），类型为 `KafkaTemplate<String, Object>`。
    - **在 `EventProducer` 中，`kafkaTemplate2` 的注入字段类型是 `KafkaTemplate<String, Object>`**，与配置类的 `kafkaTemplate` Bean 类型完全匹配。
    - **Spring 会优先按名称匹配**，但此处 `kafkaTemplate2` 字段名与 Bean 名称 `kafkaTemplate` 不一致，因此会尝试按类型匹配。由于配置类中的 Bean 是唯一的 `KafkaTemplate<String, Object>` 实例，因此 `kafkaTemplate2` 会被注入配置类的 `kafkaTemplate`。

2. **验证方法**  
   你可以在 `kafkaTemplate2` 发送消息时观察以下行为：
    - 如果消息发送的分区策略是 `CustomerPartitioner`（而非默认策略），说明 `kafkaTemplate2` 确实使用了配置类的 `kafkaTemplate`。
    - 如果消息发送时触发了 `CustomerProducerInterceptor` 拦截器，也验证了这一点。

---

### 二、**`kafkaTemplate` 的注入逻辑**
1. **类型不匹配导致自动配置生效**
    - `EventProducer` 中的 `kafkaTemplate` 字段类型是 `KafkaTemplate<String, String>`，而配置类的 `kafkaTemplate()` 方法返回的是 `KafkaTemplate<String, Object>`。**泛型类型不一致会导致 Spring 认为这是两个不同的 Bean**。
    - 因此，`kafkaTemplate` 字段会由 Spring Boot 的自动配置生成，而非你的自定义配置类。这意味着它会使用默认的序列化器（如 `StringSerializer`）和默认的分区策略（如 `DefaultPartitioner`）。

2. **验证方法**
    - 如果 `kafkaTemplate` 发送消息时未触发 `CustomerProducerInterceptor` 拦截器，或者分区策略是默认的（而非 `CustomerPartitioner`），则说明它未被配置类覆盖。

---

### 三、**如何解决冲突**
#### 方案 1：通过 `@Qualifier` 明确指定 Bean
1. **修改配置类**  
   为不同的 `KafkaTemplate` 指定名称，例如：
   ```java
   @Bean("customKafkaTemplate")
   public KafkaTemplate<String, Object> kafkaTemplate() {
       return new KafkaTemplate<>(producerFactory());
   }
   ```
2. **在 `EventProducer` 中注入**
   ```java
   @Resource
   @Qualifier("customKafkaTemplate")
   private KafkaTemplate<String, Object> kafkaTemplate2;
   ```

#### 方案 2：完全禁用自动配置
在 `application.yml` 中添加以下配置，禁用 Spring Boot 的自动生成 `KafkaTemplate`：
```yaml
spring:
  kafka:
    template:
      default-type: none
```
此时所有 `KafkaTemplate` 必须通过自定义配置类定义。

---

### 四、**当前代码的风险点**
1. **泛型类型混淆**  
   配置类的 `kafkaTemplate` 是 `KafkaTemplate<String, Object>`，而 `kafkaTemplate2` 字段的泛型类型与之匹配，导致意外覆盖。建议统一泛型类型或通过 `@Qualifier` 隔离。

2. **拦截器和分区器冲突**  
   如果 `kafkaTemplate`（自动配置）和 `kafkaTemplate2`（自定义配置）使用不同的序列化器或分区策略，可能导致消息发送异常。

---

### 总结
- **`kafkaTemplate2`**：被配置类的 `kafkaTemplate` 覆盖，使用了自定义的分区器和拦截器。
- **`kafkaTemplate`**：未被覆盖，由 Spring Boot 自动配置生成，使用默认参数。
- **建议**：通过 `@Qualifier` 或禁用自动配置明确区分不同 `KafkaTemplate`，避免隐式依赖导致问题。




---


## 自定义分配策略：XxxPartitioner （接口：Partitioner）

![img_04](./assets/img_4.png)

```java
props.put(ProducerConfig.PARTITIONER_CLASS_CONFIG, CustomerPartitioner.class);
```

```java
package com.fire.kafkaeasystudy.config;

import org.apache.kafka.clients.producer.Partitioner;
import org.apache.kafka.common.Cluster;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.utils.Utils;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class CustomerPartitioner implements Partitioner {

    private AtomicInteger nextPartition = new AtomicInteger(0);
    
    @Override
    public int partition(String topic, Object key, byte[] keyBytes, Object value, byte[] valueBytes, Cluster cluster) {
        List<PartitionInfo> partitions = cluster.partitionsForTopic(topic);
        int numPartitions = partitions.size();
        if (key == null){
            // 使用轮询方式选择分区
            int next = nextPartition.getAndIncrement();
            if (next >= numPartitions){
                nextPartition.compareAndSet(next,0);
            }
            return next;
        }else{
            // 如果key不为null,则使用默认的分区策略
            return Utils.toPositive(Utils.murmur2(keyBytes) % numPartitions);
        }
    }

    @Override
    public void close() {

    }

    @Override
    public void configure(Map<String, ?> configs) {

    }
}

```


# 生产者发送消息的流程

![img_05](./assets/img_5.png)


# 拦截生产者发送的消息

Kafka 拦截器（Interceptors）是一种允许用户在消息生产或消费的关键阶段插入自定义逻辑的插件机制，其核心思想是在不修改主业务逻辑的前提下实现可插拔的动态处理链。以下是 Kafka 拦截器的核心要点：

![img_06](./assets/img_6.png)

---

### **一、拦截器类型与核心接口**
Kafka 拦截器分为 **生产者拦截器** 和 **消费者拦截器**，分别作用于消息发送和消费的不同阶段：
1. **生产者拦截器（ProducerInterceptor）**
    - **接口方法**：
        - `onSend(ProducerRecord)`：在消息序列化、计算分区前调用，可修改消息内容或添加头信息。
        - `onAcknowledgement(RecordMetadata, Exception)`：在消息成功提交或失败时调用（早于用户回调），用于统计成功率、记录日志等。
        - `close()`：关闭拦截器时清理资源。
    - **典型场景**：消息加密、添加时间戳、统计发送成功率。

2. **消费者拦截器（ConsumerInterceptor）**
    - **接口方法**：
        - `onConsume(ConsumerRecords)`：在消息反序列化后、正式消费前调用，可过滤或修改消息。
        - `onCommit(Map<TopicPartition, OffsetAndMetadata>)`：在提交位移后调用，用于记录提交状态或审计。
    - **典型场景**：消息解密、过滤无效数据、统计消费延迟。

---

### **二、拦截器的使用步骤**
1. **实现接口**  
   创建自定义类实现 `ProducerInterceptor` 或 `ConsumerInterceptor` 接口，重写核心方法。  
   **示例**（生产者统计消息成功率）：
   ```java
   public class CountProducerInterceptor implements ProducerInterceptor<String, String> {
       private Jedis jedis; // 使用 Redis 记录统计信息
       @Override
       public ProducerRecord<String, String> onSend(ProducerRecord record) {
           jedis.incr("totalMessages");
           return record;
       }
       @Override
       public void onAcknowledgement(RecordMetadata metadata, Exception e) {
           if (e == null) jedis.incr("successMessages");
           else jedis.incr("failedMessages");
       }
   } 
   ```

2. **配置拦截器**  
   在生产者/消费者配置中通过 `interceptor.classes` 指定拦截器全限定类名，支持多个拦截器链式调用：
   ```java
   Properties props = new Properties();
   List<String> interceptors = new ArrayList<>();
   interceptors.add("com.example.CountProducerInterceptor");
   interceptors.add("com.example.TimestampInterceptor");
   props.put(ProducerConfig.INTERCEPTOR_CLASSES_CONFIG, interceptors); 
   ```

3. **启用拦截器**  
   将配置传递给 `KafkaProducer` 或 `KafkaConsumer` 实例，拦截器自动生效。

---

### **三、典型应用场景**
1. **监控与统计**
    - 记录消息端到端延迟、TPS、成功率等指标。
    - 示例：通过 `onSend` 记录发送时间戳，`onAcknowledgement` 计算处理延迟。

2. **消息审计与安全**
    - 多租户环境下追踪消息来源，防止数据泄露。
    - 示例：在消息头中添加审计标识（如租户ID）。

3. **消息内容处理**
    - 加密/解密、压缩/解压、格式转换（如添加统一前缀）。
    - 示例：在 `onConsume` 中过滤不符合业务规则的消息。

4. **性能优化**
    - 在拦截器中预计算数据，减少主业务逻辑复杂度。

---

### **四、注意事项**
1. **线程安全**
    - `onSend` 和 `onAcknowledgement` 可能运行在不同线程中，共享变量需保证线程安全。

2. **性能影响**
    - 避免在拦截器中执行耗时操作（如同步数据库写入），否则会降低 Kafka 吞吐量。

3. **版本兼容性**
    - 拦截器功能自 Kafka 0.10.0.0 引入，需确保客户端与 Broker 版本兼容。

4. **配置正确性**
    - 拦截器类名需使用全限定名（如 `com.example.MyInterceptor`），否则会因类加载失败导致异常。

---

### **五、总结**
Kafka 拦截器通过灵活的事件钩子机制，为用户提供了扩展消息处理能力的途径，尤其适用于需要统一监控、审计或内容处理的场景。使用时需注意性能与线程安全问题，合理设计拦截器逻辑以实现业务目标。

---


## 示例
```java
package com.fire.kafkaeasystudy.config;

import org.apache.kafka.clients.producer.ProducerInterceptor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import java.util.Map;

public class CustomerProducerInterceptor implements ProducerInterceptor<String,Object> {

    /**
     * 发送消息时，会先调用该方法，对消息进行拦截，可以在拦截中对消息做一些处理，记录日志等操作......
     * @param record the record from client or the record returned by the previous interceptor in the chain of interceptors.
     * @return
     */
    @Override
    public ProducerRecord<String,Object> onSend(ProducerRecord record) {
        System.out.println("拦截消息" + record.toString());
        return record;
    }

    /**
     * 服务器收到消息后的一个确认
     * @param metadata The metadata for the record that was sent (i.e. the partition and offset).
     *                 If an error occurred, metadata will contain only valid topic and maybe
     *                 partition. If partition is not given in ProducerRecord and an error occurs
     *                 before partition gets assigned, then partition will be set to RecordMetadata.NO_PARTITION.
     *                 The metadata may be null if the client passed null record to
     *                 {@link org.apache.kafka.clients.producer.KafkaProducer#send(ProducerRecord)}.
     * @param exception The exception thrown during processing of this record. Null if no error occurred.
     */
    @Override
    public void onAcknowledgement(RecordMetadata metadata, Exception exception) {
        if (metadata != null) {
            System.out.println("服务器收到该消息: " + metadata.offset());
        }else{
            System.out.println("消息发送失败了,exception:" + exception.getMessage());
        }
    }

    @Override
    public void close() {

    }

    @Override
    public void configure(Map<String, ?> configs) {

    }
}

```

```java
// 添加一个拦截器
props.put(ProducerConfig.INTERCEPTOR_CLASSES_CONFIG, CustomerProducerInterceptor.class.getName());
```

---

# 查看消费者组的实时消费状态

`kafka-consumer-groups.sh --bootstrap-server localhost:9092 --group offsetGroup --describe` 是 Kafka 提供的用于查看指定消费者组详细状态的命令行工具。以下是对该命令的逐层解析：

---

### **一、命令作用**
该命令用于查看名为 `offsetGroup` 的消费者组的实时消费状态，包括分区偏移量、消息积压量、消费者成员信息等核心指标。

---

### **二、参数说明**
1. **`--bootstrap-server localhost:9092`**
    - 指定 Kafka 集群的 Broker 地址列表，此处为本地单节点集群的默认端口（9092）。
    - 替代旧版 `--zookeeper` 参数，直接通过 Broker 获取消费者组信息。

2. **`--group offsetGroup`**
    - 指定目标消费者组名称，仅显示该组的详细信息。

3. **`--describe`**
    - 输出消费者组的订阅详情，包括每个分区的消费进度、消息积压量等。

---

### **三、输出结果解析**
执行命令后，输出的典型字段如下（以示例数据说明）：

| **字段**          | **说明**                                                                                       | **示例值**                                |
|--------------------|-----------------------------------------------------------------------------------------------|-------------------------------------------|
| **TOPIC**          | 消费者订阅的主题名称                                                                           | `order-topic`                             |
| **PARTITION**      | 分区编号（Kafka 并行处理的基本单位）                                                            | `0`                                       |
| **CURRENT-OFFSET** | 消费者组当前已提交的偏移量（最后一条已消费消息的位置）                                         | `668`                                     |
| **LOG-END-OFFSET** | 分区的日志末端偏移量（最新生产的消息位置）                                                     | `668`                                     |
| **LAG**            | 积压消息数量（`LOG-END-OFFSET - CURRENT-OFFSET`，0 表示无积压）                               | `0`                                       |
| **CONSUMER-ID**    | 消费者实例的唯一标识，格式为 `consumer-<clientId>-<UUID>`                                      | `consumer-1-063cdec2-b525-4ba3-bbfe...`   |
| **HOST**           | 消费者实例所在的服务器 IP 地址                                                                 | `/192.168.0.2`                            |
| **CLIENT-ID**      | 客户端自定义的标识符（由消费者启动时指定）                                                     | `consumer-1`                              |

---

### **四、关键场景应用**
1. **监控消息积压**
    - 若 `LAG` 值持续增长，可能因消费者处理速度不足或分区分配不均导致，需优化消费者线程池或扩容分区。
2. **定位异常消费者**
    - 通过 `CONSUMER-ID` 和 `HOST` 可追踪具体消费者实例的运行状态，排查宕机或性能瓶颈。
3. **验证分区均衡性**
    - 对比不同分区的 `LAG` 值，若差异过大，可能需调整分区数或消费者实例数量。

---

### **五、注意事项**
- **消费者组状态**：若消费者组无活跃成员，输出中可能显示 `No active members`，需结合 `--state` 参数查看组状态（如 Stable、Dead）。
- **权限要求**：需确保对 Kafka Broker 有读权限，否则可能因权限不足无法获取信息。
- **版本兼容性**：Kafka 2.0+ 版本已弃用 ZooKeeper 参数，必须使用 `--bootstrap-server`。

通过此命令，可快速诊断消费者组健康状态，为优化消息处理流程提供数据支持。如需进一步操作（如重置偏移量），可结合 `--reset-offsets` 参数实现。











