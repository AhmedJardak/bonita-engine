CREATE TABLE profile (
  tenantId BIGINT NOT NULL,
  id BIGINT NOT NULL,
  isDefault BOOLEAN NOT NULL,
  name VARCHAR(50) NOT NULL,
  description TEXT,
  iconPath VARCHAR(50),
  creationDate BIGINT NOT NULL,
  createdBy BIGINT NOT NULL,
  lastUpdateDate BIGINT NOT NULL,
  lastUpdatedBy BIGINT NOT NULL,
  UNIQUE (tenantId, name),
  PRIMARY KEY (tenantId, id)
) ENGINE = INNODB;

CREATE TABLE profileentry (
  tenantId BIGINT NOT NULL,
  id BIGINT NOT NULL,
  profileId BIGINT NOT NULL,
  name VARCHAR(50),
  description TEXT,
  parentId BIGINT,
  index_ BIGINT,
  type VARCHAR(50),
  page VARCHAR(50),
  PRIMARY KEY (tenantId, id)
) ENGINE = INNODB;

CREATE INDEX indexProfileEntry ON profileentry(tenantId, parentId, profileId);

CREATE TABLE profilemember (
  tenantId BIGINT NOT NULL,
  id BIGINT NOT NULL,
  profileId BIGINT NOT NULL,
  userId BIGINT NOT NULL,
  groupId BIGINT NOT NULL,
  roleId BIGINT NOT NULL,
  UNIQUE (tenantId, profileId, userId, groupId, roleId),
  PRIMARY KEY (tenantId, id)
) ENGINE = INNODB;