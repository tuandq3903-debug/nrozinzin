-- =============================================================
-- Cleanup data loi cho player.HocSkill
-- Nguyên nhan: ItemTemplateSkillId chua id khong ton tai trong item_template
-- => khi acc login, PlayerService.checkCompleteHocSkill() bi NullPointerException
-- Script nay: reset Time = -1 cho cac player dang co Time > 0 va Potential = 0
-- (cac acc khong dang hoc skill cung bi reset de an toan)
--
-- Chay script nay TREN DATABASE THAT (khong phai nro.sql goc)
-- Vi du: nro_zinzin
-- =============================================================

-- BUOC 1: Xem truoc cac acc bi loi (co Time > 0 nhung Potential = 0)
-- hoac co Time > 0 (dang cho hoc skill)
SELECT id, name, HocSkill, CheckHocSkill
FROM player
WHERE HocSkill IS NOT NULL
  AND HocSkill NOT IN ('[-1,-1,0]', '[0,-1,0]')
  AND HocSkill != '[-1,-1,0]'
ORDER BY id;

-- BUOC 2 (tu chay neu muon): reset Time = -1 cho moi player
-- Truoc khi chay nen backup:
--   CREATE TABLE player_backup_20260909 LIKE player;
--   INSERT INTO player_backup_20260909 SELECT * FROM player;

UPDATE player
SET HocSkill = '[-1,-1,0]'
WHERE HocSkill IS NOT NULL
  AND HocSkill != '[-1,-1,0]';

-- BUOC 3: kiem tra lai
SELECT id, name, HocSkill
FROM player
WHERE HocSkill IS NOT NULL AND HocSkill != '[-1,-1,0]';
-- (khong co dong nao -> OK)
