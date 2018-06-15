--- 判断source_str 中是否contains sub_str
--- @param source_str
--- @param sub_str
local function contains(source_str , sub_str)
    local start_pos,end_pos = string.find(source_str,sub_str);
    if start_pos == nil then
        return false
    end
    local source_str_len = string.len(source_str)
    local s = string.sub(source_str,end_pos,end_pos)
    if source_str_len == end_pos then
        return true;
    elseif string.sub(source_str,end_pos + 1,end_pos + 1) == "," then
        return true;
    end
    return false;
end


--- 获取令牌
--- 返回码
--- 0 没有令牌桶配置
--- -1 表示取令牌失败，也就是桶里没有令牌
--- 1 表示取令牌成功
--- @param key 令牌的唯一标识
--- @param permits  请求令牌数量
--- @param curr_mill_second 当前毫秒数
--- @param context 使用令牌的应用标识
local function acquire(key , permits , curr_mill_second , context)
    local rate_limit_info = redis.pcall("HMGET" , key , "last_mill_second","curr_permits","max_permits","rate","apps")
    local last_mill_second = rate_limit_info[1]
    local curr_permits = tonumber(rate_limit_info[2])
    local max_permits = tonumber(rate_limit_info[3])
    local rate = rate_limit_info[4]
    local apps = rate_limit_info[5]

    -- 标识没有配置令牌桶
    if type(apps) == 'boolean' or apps == nil or not contains(apps , context) then
        return 0
    end

    local local_curr_permits = curr_permits


    --- 令牌桶刚刚创建，上一次获取令牌的毫秒数为空
    --- 根据和上一次向桶里添加令牌的时间和当前时间差，触发式往桶里添加令牌，并且更新上一次向桶里添加令牌的时间
    --- 如果向桶里添加的令牌数不足一个，则不更新上一次向桶里添加令牌的时间
    if (type(last_mill_second) ~= 'boolean' and last_mill_second ~= nil) then
        local reverse_permits = math.floor(((curr_mill_second - last_mill_second)/1000) * rate)
        local expect_curr_permits = reverse_permits + curr_permits;
        local_curr_permits = math.min(expect_curr_permits,max_permits)
        --- 大于0表示不是第一次获取令牌，也没有向桶里添加令牌
        if (reverse_permits > 0) then
            redis.pcall("HSET",key, "last_mill_second" , curr_mill_second)
        end
    else
        redis.pcall("HSET" , key, "last_mill_second" , curr_mill_second)
    end
    local result = -1
    if (local_curr_permits - permits >=0) then
        result = 1
        redis.pcall("HSET" , key , "curr_permits" , local_curr_permits - permits)
    else
        redis.pcall("HSET" , key , "curr_permits" , local_curr_permits)
    end
    return result
end


--- 初始化令牌桶配置
--- @param key 令牌的唯一标识
--- @param max_permits 桶大小
--- @param rate  向桶里添加令牌的速率
--- @param apps  可以使用令牌桶的应用列表，应用之前用逗号分隔
local function init(key, max_permits, rate , apps)
    local rate_limit_info = redis.pcall("HMGET",key,"last_mill_second","curr_permits","max_permits","rate","apps")
    local org_max_permits = tonumber(rate_limit_info[3])
    local org_rate = rate_limit_info[4]
    local org_apps = rate_limit_info[5]
    if (org_max_permits == nil) or (apps ~= org_apps or rate ~= org_rate or max_permits ~= org_max_permits) then
        redis.pcall("HMSET", key , "max_permits" , max_permits , "rate" , rate , "curr_permits" , max_permits , "apps" , apps)
    end
    return 1
end

--- 删除令牌桶
local function delete(key)
    redis.pcall("DEL",key)
    return 1
end


local key = KEYS[1]
local method = ARGV[1]
if method == 'acquire' then
    return acquire(key,KEYS[2],KEYS[3],KEYS[4])
elseif method == 'init' then
    return init(key,KEYS[2],KEYS[3],KEYS[4])
elseif method == 'delete' then
    return delete(key)
end


