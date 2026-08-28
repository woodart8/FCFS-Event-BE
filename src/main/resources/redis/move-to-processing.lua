-- queue에서 가장 앞의 요청 1개 가져오기
local result = redis.call(
    'ZRANGE',
    KEYS[1],
    0,
    0,
    'WITHSCORES'
)

if #result == 0 then
    return nil
end

local requestId = result[1]
local sequence = result[2]

-- queue에서 제거
redis.call(
    'ZREM',
    KEYS[1],
    requestId
)

-- processing에 등록
-- score는 처리 시작 시간
redis.call(
    'ZADD',
    KEYS[2],
    redis.call('TIME')[1],
    requestId
)

return requestId