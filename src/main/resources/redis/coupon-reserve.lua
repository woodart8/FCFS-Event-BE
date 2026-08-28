local usersKey = KEYS[1]
local issuedKey = KEYS[2]
local maxKey = KEYS[3]
local queueKey = KEYS[4]

local userId = ARGV[1]
local requestId = ARGV[2]

-- 중복 요청 확인 및 등록
local added = redis.call('SADD', usersKey, userId)

if added == 0 then
    return -1
end

-- 발급 수량 증가
local issued = redis.call('INCR', issuedKey)

-- 최대 수량 조회
local max = tonumber(redis.call('GET', maxKey))

if max == nil then
    redis.call('SREM', usersKey, userId)
    redis.call('DECR', issuedKey)

    return -2
end

-- 선착순 성공
if issued <= max then

    -- 쿠폰 발급 대기열에 등록
    redis.call(
        'ZADD',
        queueKey,
        issued,
        requestId
    )

    return 1
end

-- 선착순 탈락
redis.call('SREM', usersKey, userId)
redis.call('DECR', issuedKey)

return 0