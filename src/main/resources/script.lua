redis.call("SET",KEYS[1],ARGV[1])
local current = redis.call('GET', KEYS[1])
return current
