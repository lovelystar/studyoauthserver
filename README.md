MySQL 8 버전 이상 사용.

사용할 테이블 생성
*********************************************************************************************************************************************
CREATE TABLE \`authorities\` (
  \`idx\` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  \`username\` varchar(128) NOT NULL,
  \`authority\` varchar(128) NOT NULL,
  \`groupId\` bigint(20) DEFAULT NULL,
  PRIMARY KEY (\`idx\`),
  UNIQUE KEY \`authorities_unique\` (\`username\`,\`authority\`),
  CONSTRAINT \`authorities_fk1\` FOREIGN KEY (\`username\`) REFERENCES \`users\` (\`username\`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4

CREATE TABLE \`group_authorities\` (
  \`idx\` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  \`group_id\` bigint(20) NOT NULL,
  \`authority\` varchar(50) NOT NULL,
  PRIMARY KEY (\`idx\`),
  KEY \`fk_group_authorities_group\` (\`group_id\`),
  CONSTRAINT \`fk_group_authorities_group\` FOREIGN KEY (\`group_id\`) REFERENCES \`groups\` (\`id\`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4

CREATE TABLE \`group_members\` (
  \`id\` bigint(20) NOT NULL AUTO_INCREMENT,
  \`username\` varchar(50) NOT NULL,
  \`group_id\` bigint(20) NOT NULL,
  PRIMARY KEY (\`id\`),
  KEY \`fk_group_members_group\` (\`group_id\`),
  CONSTRAINT \`fk_group_members_group\` FOREIGN KEY (\`group_id\`) REFERENCES \`groups\` (\`id\`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4

CREATE TABLE \`groups\` (
  \`id\` bigint(20) NOT NULL AUTO_INCREMENT,
  \`group_name\` varchar(50) NOT NULL,
  PRIMARY KEY (\`id\`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4

CREATE TABLE \`oauth_access_token` (
  \`token_id\` varchar(255) DEFAULT NULL,
  \`token\` blob,
  \`authentication_id\` varchar(255) DEFAULT NULL,
  \`user_name\` varchar(255) DEFAULT NULL,
  \`client_id\` varchar(255) DEFAULT NULL,
  \`authentication\` blob,
  \`refresh_token\` varchar(255) DEFAULT NULL,
  KEY \`oauth_access_token_id\` (\`token_id\`),
  KEY \`oauth_refresh_token_id\` (\`token_id\`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8

CREATE TABLE \`oauth_approvals\` (
  \`userId\` varchar(256) DEFAULT NULL,
  \`clientId\` varchar(256) DEFAULT NULL,
  \`scope\` varchar(256) DEFAULT NULL,
  \`status\` varchar(10) DEFAULT NULL,
  \`expiresAt\` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  \`lastModifiedAt\` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00'
) ENGINE=InnoDB DEFAULT CHARSET=utf8

CREATE TABLE \`oauth_client_details\` (
  \`client_id\` varchar(100) NOT NULL,
  \`resource_ids\` varchar(256) DEFAULT NULL,
  \`client_secret\` varchar(256) DEFAULT NULL,
  \`scope\` varchar(256) DEFAULT NULL,
  \`authorized_grant_types\` varchar(256) DEFAULT NULL,
  \`web_server_redirect_uri\` varchar(256) DEFAULT NULL,
  \`authorities\` varchar(256) DEFAULT NULL,
  \`access_token_validity\` int(11) DEFAULT NULL,
  \`refresh_token_validity\` int(11) DEFAULT NULL,
  \`additional_information\` varchar(4096) DEFAULT NULL,
  \`autoapprove\` varchar(256) DEFAULT NULL,
  PRIMARY KEY (\`client_id\`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4

CREATE TABLE \`oauth_client_token\` (
  \`token_id\` varchar(255) DEFAULT NULL,
  \`token\` blob,
  \`authentication_id\` varchar(255) DEFAULT NULL,
  \`user_name\` varchar(255) DEFAULT NULL,
  \`client_id\` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8

CREATE TABLE \`oauth_code\` (
  \`code\` varchar(255) DEFAULT NULL,
  \`authentication\` blob
) ENGINE=InnoDB DEFAULT CHARSET=utf8

CREATE TABLE \`oauth_refresh_token\` (
  \`token_id\` varchar(255) DEFAULT NULL,
  \`token\` blob,
  \`authentication\` blob
) ENGINE=InnoDB DEFAULT CHARSET=utf8

CREATE TABLE \`oauth_resource\` (
  \`idx\` bigint(10) unsigned NOT NULL AUTO_INCREMENT,
  \`resource_id\` varchar(50) DEFAULT NULL,
  \`resource_name\` varchar(50) DEFAULT NULL,
  \`resource_pattern\` varchar(100) DEFAULT NULL,
  \`resource_type\` varchar(10) DEFAULT NULL,
  \`sort_order\` int(10) DEFAULT NULL,
  \`httpmethod\` varchar(10) DEFAULT NULL,
  PRIMARY KEY (\`idx\`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4

CREATE TABLE \`oauth_resource_authority\` (
  \`idx\` bigint(10) unsigned NOT NULL AUTO_INCREMENT,
  \`resource_id\` varchar(50) DEFAULT NULL,
  \`authority\` varchar(20) DEFAULT NULL,
  \`username\` varchar(30) DEFAULT NULL,
  PRIMARY KEY (\`idx\`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4

CREATE TABLE \`persistent_logins\` (
  \`username\` varchar(64) NOT NULL,
  \`series\` varchar(64) NOT NULL,
  \`token\` varchar(64) NOT NULL,
  \`last_used\` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (\`series\`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4

CREATE TABLE \`SPRING_SESSION\` (
  \`PRIMARY_ID\` char(36) NOT NULL,
  \`SESSION_ID\` char(36) NOT NULL,
  \`CREATION_TIME\` bigint(20) NOT NULL,
  \`LAST_ACCESS_TIME\` bigint(20) NOT NULL,
  \`MAX_INACTIVE_INTERVAL\` int(11) NOT NULL,
  \`EXPIRY_TIME\` bigint(20) NOT NULL,
  \`PRINCIPAL_NAME\` varchar(100) DEFAULT NULL,
  PRIMARY KEY (\`PRIMARY_ID\`),
  UNIQUE KEY \`SPRING_SESSION_IX1\` (\`SESSION_ID\`),
  KEY \`SPRING_SESSION_IX2\` (\`EXPIRY_TIME\`),
  KEY \`SPRING_SESSION_IX3\` (\`PRINCIPAL_NAME\`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC

CREATE TABLE \`SPRING_SESSION_ATTRIBUTES\` (
  \`SESSION_PRIMARY_ID\` char(36) NOT NULL,
  \`ATTRIBUTE_NAME\` varchar(190) NOT NULL,
  \`ATTRIBUTE_BYTES\` blob NOT NULL,
  PRIMARY KEY (\`SESSION_PRIMARY_ID`,`ATTRIBUTE_NAME\`),
  CONSTRAINT \`SPRING_SESSION_ATTRIBUTES_FK\` FOREIGN KEY (\`SESSION_PRIMARY_ID\`) REFERENCES \`SPRING_SESSION\` (\`PRIMARY_ID\`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC

CREATE TABLE \`users\` (
  \`username\` varchar(128) NOT NULL,
  \`email\` varchar(100) NOT NULL,
  \`password\` varchar(128) NOT NULL,
  \`enabled\` char(1) NOT NULL,
  PRIMARY KEY (\`username\`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4

*********************************************************************************************************************************************


jks 생성 keytool 사용법 ( 2019. 11. 13 수정 )
*********************************************************************************************************************************************
환경변수 설정이 잘 되었다면 cmd에서 keytool을 사용할 수 있다.
keytool -genkeypair -alias name -keyalg RSA -dname "CN=Web Server,OU=Unit,O=Organization,L=City,S=State,C=US" -keypass name2019 -keystore name.jks -storepass name2019
만들어진 jks파일 가져다가 사용하면 됩니다.

(추가) openssl 사용하여 publicKey를 txt파일로 저장하기
1. jks파일을 이용하여 cer파일 생성 ( keytool -export -keystore name.jks -alias name -file public.cer )
2. openssl을 이용하여 publicKey확인 ( openssl x509 -inform der -in public.cer -pubkey -noout )

Tip. openssl 설치를 한 후 openssl실행파일에 있는 경로에 cer파일을 복사해두면 명령어 작성 시 경로까지 입력할 필요 없다.

경로 = cmd처음 접속했을 때 보여지는 C:\User\ .... 
*********************************************************************************************************************************************

! 해결해야 되는 사항
JdbcUserDetailsManager에서 setEnableGroups부분이
MySQL 8버전 이상은 그냥 groups가 아닌 `groups`로 호출해야만 에러가 나지 않기 때문에
일단은 그룹기능 사용하지 않도록 setEnableGroups(false)로 변경.

MySQL 8버전 이하는 true로 해도 괜찮다.

