set search_path = 'hhschool';

-- 1 
SELECT organization_form -- DISTINCT?
FROM employer
WHERE organization_form IS NOT NULL
ORDER BY organization_form DESC;

-- 2
SELECT organization_form, COUNT(*) AS cnt
FROM employer
GROUP BY organization_form
HAVING COUNT(*) >= 3
ORDER BY cnt DESC;

-- 3
SELECT name, site_id
FROM translation
WHERE ui = true AND lang = 'RU'
EXCEPT
SELECT name, site_id
FROM translation
WHERE lang = 'UA';

-- 4
-- hope there is a better way to do this
SELECT user_all.*
FROM 
(SELECT th.user_id, MIN(th.modification_time) as mt
FROM translation_history th 
INNER JOIN translation t
ON (th.name = t.name AND th.site_id = t.site_id AND th.lang = t.lang) 
WHERE t.ui = true
GROUP BY th.user_id) as user_min
INNER JOIN
(SELECT DISTINCT ON (th.user_id, th.modification_time) th.*
FROM translation_history th
INNER JOIN translation t
ON (th.name = t.name AND th.site_id = t.site_id AND th.lang = t.lang) 
WHERE t.ui = true) as user_all
ON user_min.user_id = user_all.user_id AND user_min.mt = user_all.modification_time
ORDER BY user_all.modification_time LIMIT 10
