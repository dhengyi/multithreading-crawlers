# 多线程爬虫--抓取淘宝商品详情页URL

本项目是一个Java编写的多线程爬虫系统。此系统与我之前开发的[ip-proxy-pools-regularly][1]结合使用，共抓取了淘宝近3000个页面，从中解析到了近9万的商品详情页URL。

我并没有直接将这些商品详情页中最具价值的数据（商品信息）提取出来，因为这些富有价值的数据对于目前的我来说并不是特别具有吸引力。开发这个项目当初的本意也只是为了锻炼自己开发多线程应用程序的能力，并且真正的与反爬虫做对抗，最终我成功了～

我会将抓取到的数据（近9万商品详情页URL）提供给大家，如果大家需要真正的商品信息，而你们又没有什么好的办法，那么就花半天时间阅读一下此项目的源码吧，最后只要在这个代码的框架上稍作修改，这个多线程爬虫系统将完全满足你们的需求。

## 环境需求

> - JDK 1.8
> - MySQL
> - Redis
> - IDEA
> - Maven

## 实现架构

包名 | 功能
---|---
database | 有关MySQL与Redis数据库的配置类及操作类
httpbrower | 发送HTTP请求，接收Response相关类
ipproxypool | IP代理池
mainmethod | Main方法入口
mythread | 项目相关线程类
parse | 网页源码解析类
urlbuild | URL创建类
utilclass | 工具类

关于项目实现的技术细节，本人打算另写一篇博客来进行专门的讲解，到时给这里再贴上超链... ...

## 使用说明
MySQL配置文件下载（附带数据）：
[![xiyoulinux.sql][4]][2]

## TODO
1. 项目中抓取带有页面参数的商品搜索页URL及商品详情页URL会产生死锁，分别是近2000与近4000数量的待抓取任务，然而每次都会剩余不到10个任务无法成功抓取，目前猜测有可能是死锁，也有可能是由HttpClient包引起的未知bug
2. 线程调度，任务分配，线程安全这三方面还需要不断优化与完善
3. 爬虫并不智能，考虑开发自动化智能爬虫
4. 考虑将此系统设计成一个爬虫框架，可让用户指定任务进行抓取
5. 可视化处理... ...

## 版本说明
![version 1.0][3]


  [1]: https://github.com/championheng/ip-proxy-pools-regularly/tree/master/ip%E4%BB%A3%E7%90%86%E4%B8%8E%E5%AE%9A%E7%82%B9%E7%88%AC%E5%8F%96%28%E9%87%8D%E6%9E%84%29
  [2]: https://1drv.ms/u/s!Alo1-VlEZGPPdzh2W4s-Nvdhvzs
  [3]: https://img.shields.io/badge/version-1.0-blue.svg
  [4]: https://img.shields.io/badge/download-MySQL-brightgreen.svg
