CREATE TABLE app_user (
    id SERIAL PRIMARY KEY,
	username VARCHAR,
	hashed_password VARCHAR NOT NULL,
	email VARCHAR NOT NULL,
	created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE artist(
	id SERIAL PRIMARY KEY,
	mbid VARCHAR(36) UNIQUE,
	artist_name VARCHAR NOT NULL
);

CREATE TABLE release_group (
	id SERIAL PRIMARY KEY,
	mbid VARCHAR(36) UNIQUE,
	artist_id INTEGER REFERENCES artist(id) NOT NULL,
	title VARCHAR NOT NULL,
	release_date VARCHAR,
	format VARCHAR
);

CREATE TABLE track (
	id SERIAL PRIMARY KEY,
	mbid VARCHAR(36) UNIQUE,
	artist_id INTEGER REFERENCES artist(id),
	title VARCHAR NOT NULL,
	release_date VARCHAR,
	cover_release_mbid VARCHAR(36)
);
		
CREATE TABLE track_release (
	track_id INTEGER REFERENCES track(id) NOT NULL,
	release_id INTEGER REFERENCES release_group(id),
	PRIMARY KEY (track_id, release_id)
);

CREATE TABLE scrobble(
	id SERIAL PRIMARY KEY,
	user_id INTEGER REFERENCES app_user(id) NOT NULL,
	track_id INTEGER REFERENCES track(id) NOT NULL,
	release_id INTEGER REFERENCES release_group(id),
	first_listened_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
);

CREATE TABLE release_rating(
	user_id INTEGER REFERENCES app_user(id) NOT NULL,
	release_id INTEGER REFERENCES release_group(id) NOT NULL,
	rating INTEGER check (rating between 0 and 10),
	rated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	PRIMARY KEY (user_id, release_id)
);

CREATE TABLE track_rating(
	user_id INTEGER REFERENCES app_user(id) NOT NULL,
	track_id INTEGER REFERENCES track(id) NOT NULL,
	rating INTEGER check (rating between 0 and 10),
	rated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	PRIMARY KEY (user_id, track_id)
);

CREATE TABLE top5_releases(
	user_id INTEGER REFERENCES app_user(id) NOT NULL,
	release1_id INTEGER references release_group(id),
	release2_id INTEGER references release_group(id),
	release3_id INTEGER references release_group(id),
	release4_id INTEGER references release_group(id),
	release5_id INTEGER references release_group(id)
);

SELECT * FROM track_release;
CREATE OR REPLACE FUNCTION update_any_timestamp()
RETURNS TRIGGER AS $$
BEGIN
    CASE TG_TABLE_NAME
        WHEN 'scrobble' THEN NEW.first_listened_at := CURRENT_TIMESTAMP;
        WHEN 'release_rating' THEN NEW.rated_at := CURRENT_TIMESTAMP;
        WHEN 'track_rating' THEN NEW.rated_at := CURRENT_TIMESTAMP;
    END CASE;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Use same function for all three tables
CREATE TRIGGER update_scrobble_timestamp
BEFORE UPDATE ON scrobble
FOR EACH ROW
EXECUTE FUNCTION update_any_timestamp();

CREATE TRIGGER update_release_rating_timestamp
BEFORE UPDATE ON release_rating
FOR EACH ROW
EXECUTE FUNCTION update_any_timestamp();

CREATE TRIGGER update_track_rating_timestamp
BEFORE UPDATE ON track_rating
FOR EACH ROW
EXECUTE FUNCTION update_any_timestamp();