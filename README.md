Redis + Lua 抢红包程序设计

红包生成程序： com.qf58.exec.redpackage.PackageGenerage

抢红包程序： com.qf58.exec.redpackage.RedDraw

主Lua脚本 ： red/red.lua




环境：

单机Redis

CentOS + 32G内存

拆分10000红包

约1s抢完


利用Redis + lua 实现(核心代码实现)企业级分布式限流的案例：
lua脚本详见：ratelimiter/rate_limiter.lua
初始化脚本：      com.qf58.exec.ratelimiter.RateLimiterInitialization
限流测试脚本：    com.qf58.exec.ratelimiter.RateLimiterAcquire
