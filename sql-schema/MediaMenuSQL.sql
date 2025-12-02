CREATE TABLE app_user (
    id SERIAL PRIMARY KEY,
	username VARCHAR,
	hashed_password VARCHAR NOT NULL,
	email VARCHAR NOT NULL,
	created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);	

CREATE TABLE user_spotify(
	user_id INTEGER PRIMARY KEY REFERENCES app_user(id) NOT NULL,
	access_token VARCHAR,
	access_token_expiry INTEGER,
	refresh_token VARCHAR,
	display_name VARCHAR
);

CREATE TABLE artist(
	id SERIAL PRIMARY KEY,
	mbid VARCHAR(36) UNIQUE,
	spotify_id VARCHAR UNIQUE,
	artist_name VARCHAR NOT NULL
);

CREATE TABLE release_group (
	id SERIAL PRIMARY KEY,
	mbid VARCHAR(36) UNIQUE,
	spotify_id VARCHAR UNIQUE,
	title VARCHAR NOT NULL,
	release_date VARCHAR,
	format VARCHAR
);

CREATE TABLE track (
	id SERIAL PRIMARY KEY,
	mbid VARCHAR(36) UNIQUE,
	spotify_id VARCHAR UNIQUE,
	isrc VARCHAR UNIQUE,
	title VARCHAR NOT NULL,
	release_date VARCHAR,
	cover_release_mbid VARCHAR(36)
);

CREATE TABLE genre (
	id SERIAL PRIMARY KEY,
	mbid VARCHAR(36) UNIQUE,
	genre_name VARCHAR NOT NULL
);

CREATE TABLE track_release (
	track_id INTEGER REFERENCES track(id) NOT NULL,
	release_id INTEGER REFERENCES release_group(id),
	PRIMARY KEY (track_id, release_id)
);

CREATE TABLE track_genre (
	track_id INTEGER REFERENCES track(id) NOT NULL,
	genre_id INTEGER REFERENCES genre(id) NOT NULL,
	PRIMARY KEY (track_id, genre_id)
);

CREATE TABLE artist_genre (
	artist_id INTEGER REFERENCES artist(id) NOT NULL,
	genre_id INTEGER REFERENCES genre(id) NOT NULL,
	PRIMARY KEY (artist_id, genre_id)
);

CREATE TABLE release_genre (
	release_id INTEGER REFERENCES release_group(id) NOT NULL,
	genre_id INTEGER REFERENCES genre(id) NOT NULL,
	PRIMARY KEY (release_id, genre_id)
);

CREATE TABLE scrobble(
	id SERIAL PRIMARY KEY,
	user_id INTEGER REFERENCES app_user(id) NOT NULL,
	track_id INTEGER REFERENCES track(id) NOT NULL,
	release_id INTEGER REFERENCES release_group(id),
	first_listened_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE release_rating(
	user_id INTEGER REFERENCES app_user(id) NOT NULL,
	release_id INTEGER REFERENCES release_group(id) NOT NULL,
	rating NUMERIC(3,1) CHECK (rating >= 0 AND rating <= 10 AND rating = ROUND(rating,1)),
	rated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	PRIMARY KEY (user_id, release_id)
);

CREATE TABLE track_rating(
	user_id INTEGER REFERENCES app_user(id) NOT NULL,
	track_id INTEGER REFERENCES track(id) NOT NULL,
	rating NUMERIC(3,1) CHECK (rating >= 0 AND rating <= 10 AND rating = ROUND(rating,1)),
	rated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	PRIMARY KEY (user_id, track_id)
);

	
CREATE TABLE top5_releases(
	id SERIAL PRIMARY KEY,
	user_id INTEGER REFERENCES app_user(id) NOT NULL,
	tier INTEGER CHECK (tier BETWEEN 1 AND 5),
	release_id INTEGER REFERENCES release_group(id) NOT NULL
);

CREATE TABLE top5_tracks(
	id SERIAL PRIMARY KEY,
	user_id INTEGER REFERENCES app_user(id) NOT NULL,
	tier INTEGER CHECK (tier BETWEEN 1 AND 5),
	track_id INTEGER REFERENCES track(id) NOT NULL
);

CREATE TABLE release_artist(
	release_id INTEGER REFERENCES release_group(id) NOT NULL,
	artist_id INTEGER REFERENCES artist(id) NOT NULL,
	PRIMARY KEY (release_id, artist_id)
);

CREATE TABLE track_artist(
	track_id INTEGER REFERENCES track(id) NOT NULL,
	artist_id INTEGER REFERENCES artist(id) NOT NULL,
	PRIMARY KEY (track_id, artist_id)
);
	
ALTER TABLE top5_tracks ADD CONSTRAINT unique_user_track_tier UNIQUE (user_id, tier);
ALTER TABLE top5_releases ADD CONSTRAINT unique_user_release_tier UNIQUE (user_id, tier);


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