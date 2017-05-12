CREATE DATABASE tenant_a
    WITH
    OWNER = tenant
    ENCODING = 'UTF8'
    LC_COLLATE = 'Italian_Italy.1252'
    LC_CTYPE = 'Italian_Italy.1252'
    TABLESPACE = pg_default
    CONNECTION LIMIT = -1;

-- Table: public.users

-- DROP TABLE public.users;

-- Table: public.users

-- DROP TABLE public.users;

CREATE TABLE public.users
(
    username character varying(256) COLLATE pg_catalog."default" NOT NULL,
    password character varying COLLATE pg_catalog."default" NOT NULL,
    password_salt character varying(256) COLLATE pg_catalog."default",
    tenant character varying COLLATE pg_catalog."default" NOT NULL,
    CONSTRAINT pk_users PRIMARY KEY (username)
)
WITH (
    OIDS = FALSE
)
TABLESPACE pg_default;

ALTER TABLE public.users
    OWNER to tenant;

ALTER TABLE public.users
    OWNER to tenant;

-- Table: public.user_roles

-- DROP TABLE public.user_roles;

CREATE TABLE public.user_roles
(
    username character varying(256) COLLATE pg_catalog."default",
    role_name character varying(256) COLLATE pg_catalog."default"
)
WITH (
    OIDS = FALSE
)
TABLESPACE pg_default;

ALTER TABLE public.user_roles
    OWNER to tenant;

-- Table: public.roles_permissions

-- DROP TABLE public.roles_permissions;

CREATE TABLE public.roles_permissions
(
    role_name character varying(256) COLLATE pg_catalog."default",
    permission character varying(256) COLLATE pg_catalog."default"
)
WITH (
    OIDS = FALSE
)
TABLESPACE pg_default;

ALTER TABLE public.roles_permissions
    OWNER to tenant;


INSERT INTO public.users(
username, password, password_salt, tenant)
VALUES ('user_1', '04f8996da763b7a969b1028ee3007569eaf3a635486ddab211d512c85b9df8fb', 'tmp', 'tenant_a');
