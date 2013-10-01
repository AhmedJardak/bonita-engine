CREATE TABLE job_desc (
  tenantid NUMBER(19, 0) NOT NULL,
  id NUMBER(19, 0) NOT NULL,
  jobclassname VARCHAR2(100) NOT NULL,
  jobname VARCHAR2(100) NOT NULL,
  description VARCHAR2(50),
  PRIMARY KEY (tenantid, id)
);

CREATE TABLE job_param (
  tenantid NUMBER(19, 0) NOT NULL,
  id NUMBER(19, 0) NOT NULL,
  jobDescriptorId NUMBER(19, 0) NOT NULL,
  key_ VARCHAR2(50) NOT NULL,
  value_ BLOB NOT NULL,
  PRIMARY KEY (tenantid, id)
);

CREATE TABLE job_log (
  tenantid NUMBER(19, 0) NOT NULL,
  id NUMBER(19, 0) NOT NULL,
  jobDescriptorId NUMBER(19, 0) NOT NULL,
  retryNumber NUMBER(19, 0),
  lastUpdateDate NUMBER(19, 0),
  lastMessage CLOB,
  UNIQUE (tenantId, jobDescriptorId),
  PRIMARY KEY (tenantid, id)
);

ALTER TABLE job_param ADD CONSTRAINT fk_job_param_jobid FOREIGN KEY (tenantid, jobDescriptorId) REFERENCES job_desc(tenantid, id) ON DELETE CASCADE;
ALTER TABLE job_log ADD CONSTRAINT fk_job_log_jobid FOREIGN KEY (tenantid, jobDescriptorId) REFERENCES job_desc(tenantid, id) ON DELETE CASCADE;
