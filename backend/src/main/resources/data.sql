-- AI吵架生成器 种子数据
-- 由 Spring Boot spring.sql.init 在启动时自动执行

MERGE INTO t_sensitive_word (word, level) KEY (word) VALUES ('杀人', 1);
MERGE INTO t_sensitive_word (word, level) KEY (word) VALUES ('自杀', 1);
MERGE INTO t_sensitive_word (word, level) KEY (word) VALUES ('毒品', 1);
MERGE INTO t_sensitive_word (word, level) KEY (word) VALUES ('赌博', 1);
MERGE INTO t_sensitive_word (word, level) KEY (word) VALUES ('嫖娼', 1);
MERGE INTO t_sensitive_word (word, level) KEY (word) VALUES ('卖淫', 1);
MERGE INTO t_sensitive_word (word, level) KEY (word) VALUES ('恐怖袭击', 1);
MERGE INTO t_sensitive_word (word, level) KEY (word) VALUES ('颠覆国家', 1);
MERGE INTO t_sensitive_word (word, level) KEY (word) VALUES ('分裂国家', 1);
MERGE INTO t_sensitive_word (word, level) KEY (word) VALUES ('法轮功', 1);
MERGE INTO t_sensitive_word (word, level) KEY (word) VALUES ('傻逼', 2);
MERGE INTO t_sensitive_word (word, level) KEY (word) VALUES ('操你', 2);
MERGE INTO t_sensitive_word (word, level) KEY (word) VALUES ('妈的', 2);
MERGE INTO t_sensitive_word (word, level) KEY (word) VALUES ('去死', 2);
MERGE INTO t_sensitive_word (word, level) KEY (word) VALUES ('废物', 2);
