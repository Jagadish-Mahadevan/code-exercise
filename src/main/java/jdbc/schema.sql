drop table event;
CREATE TABLE EVENT (ID VARCHAR(128), STATE VARCHAR(10), TYPE VARCHAR(15), HOST VARCHAR(30), TIMESTAMP BIGINT, ALERT BOOLEAN DEFAULT FALSE NOT NULL);