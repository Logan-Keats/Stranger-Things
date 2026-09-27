BEGIN TRANSACTION;
CREATE TABLE IF NOT EXISTS "audit_log" (
	"id"	INTEGER,
	"event_type"	TEXT NOT NULL,
	"action"	TEXT NOT NULL,
	"username"	TEXT NOT NULL,
	"resource_id"	INTEGER,
	"resource_name"	TEXT,
	"occurred_at"	TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP,
	PRIMARY KEY("id" AUTOINCREMENT)
);
CREATE TABLE IF NOT EXISTS "bookings" (
	"id"	INTEGER,
	"resource_id"	INTEGER NOT NULL,
	"resource_name"	TEXT NOT NULL,
	"borrower_username"	TEXT NOT NULL,
	"start_date"	TEXT NOT NULL,
	"end_date"	TEXT NOT NULL,
	"status"	TEXT NOT NULL,
	"created_at"	TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP,
	PRIMARY KEY("id" AUTOINCREMENT)
);
CREATE TABLE IF NOT EXISTS "items" (
	"id"	INTEGER,
	"name"	TEXT NOT NULL,
	"category"	TEXT NOT NULL,
	"status"	TEXT NOT NULL,
	"estimated_savings"	REAL NOT NULL DEFAULT 0,
	"co2_avoided_kg"	REAL NOT NULL DEFAULT 0,
	"owner_username"	TEXT NOT NULL DEFAULT '',
	"description"	TEXT NOT NULL DEFAULT '',
	"collection_method"	TEXT NOT NULL DEFAULT '',
	PRIMARY KEY("id" AUTOINCREMENT)
);
CREATE TABLE IF NOT EXISTS "users" (
	"id"	INTEGER,
	"username"	TEXT NOT NULL UNIQUE,
	"password_hash"	TEXT NOT NULL DEFAULT '',
	"role"	TEXT NOT NULL DEFAULT 'MEMBER',
	"created_at"	TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP,
	PRIMARY KEY("id" AUTOINCREMENT)
);
INSERT INTO "audit_log" VALUES (1,'Requested','Booking request','Sam',1,'Cordless Drill','2026-08-24 09:10:00');
INSERT INTO "audit_log" VALUES (2,'Returned','Booking returned','Sam',1,'Cordless Drill','2026-08-25 16:30:00');
INSERT INTO "audit_log" VALUES (3,'Requested','Booking request','member',2,'Lawn Mower','2026-08-25 10:20:00');
INSERT INTO "audit_log" VALUES (4,'Approved','Booking approved','admin',2,'Lawn Mower','2026-08-25 10:40:00');
INSERT INTO "audit_log" VALUES (5,'Requested','Booking request','Alex',3,'3D Printer','2026-08-27 17:10:00');
INSERT INTO "bookings" VALUES (1,1,'Cordless Drill','Sam','2026-08-24','2026-08-25','RETURNED','2026-08-24 09:10:00');
INSERT INTO "bookings" VALUES (2,2,'Lawn Mower','member','2026-08-25','2026-08-27','ON_LOAN','2026-08-25 10:20:00');
INSERT INTO "bookings" VALUES (3,3,'3D Printer','Alex','2026-08-27','2026-08-29','APPROVED','2026-08-27 17:10:00');
INSERT INTO "items" VALUES (1,'Cordless Drill','Tools','ACTIVE',45.0,5.0,'David','Cordless power drill suitable for basic household repairs and DIY projects.','Pick Up');
INSERT INTO "items" VALUES (2,'Lawn Mower','Garden Equipment','ACTIVE',32.5,4.2,'Sarah','Electric lawn mower suitable for small to medium-sized lawns.','Pick Up');
INSERT INTO "items" VALUES (3,'3D Printer','Electronics','ACTIVE',55.0,9.3,'Michael','3D printer available for small personal projects and prototype printing.','Pick Up');
INSERT INTO "users" VALUES (1,'admin','8c6976e5b5410415bde908bd4dee15dfb167a9c873fc4bb8a81f6f2ab448a918','ADMIN','2026-08-20 09:00:00');
INSERT INTO "users" VALUES (2,'member','e31ab643c44f7a0ec824b59d1194d60dac334200d845e61d2d289daa0f087ea4','MEMBER','2026-08-21 09:00:00');
INSERT INTO "users" VALUES (3,'Sam','e96e02d8e47f2a7c03be5117b3ed175c52aa30fb22028cf9c96f261563577605','MEMBER','2026-08-22 09:00:00');
INSERT INTO "users" VALUES (4,'Alex','4135aa9dc1b842a653dea846903ddb95bfb8c5a10c504a7fa16e10bc31d1fdf0','MEMBER','2026-08-23 09:00:00');
INSERT INTO "users" VALUES (5,'David','07d046d5fac12b3f82daf5035b9aae86db5adc8275ebfbf05ec83005a4a8ba3e','MEMBER','2026-08-24 09:00:00');
INSERT INTO "users" VALUES (6,'Sarah','d233633d9524e84c71d6fe45eb3836f8919148e4a5fc2234cc9e6494ec0f11c2','MEMBER','2026-08-25 09:00:00');
INSERT INTO "users" VALUES (7,'Michael','34550715062af006ac4fab288de67ecb44793c3a05c475227241535f6ef7a81b','MEMBER','2026-08-26 09:00:00');
COMMIT;
