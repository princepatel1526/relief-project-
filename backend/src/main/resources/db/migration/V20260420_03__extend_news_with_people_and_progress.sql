ALTER TABLE news_updates
  ADD COLUMN IF NOT EXISTS affected_people INT NULL,
  ADD COLUMN IF NOT EXISTS rescue_progress INT NULL;
