CREATE SEQUENCE IBS_ID_SEQ START WITH 81383747923 nomaxvalue;

CREATE SEQUENCE IBS_ID_SEQ START WITH 81383747923 nomaxvalue;

create table Z#ISNA_EXCH_BUFFER
(
  id         NUMBER,
  sn         NUMBER,
  su         NUMBER,
  c_inout    VARCHAR2(1),
  c_data     BLOB,
  c_date_msg DATE
)
;

alter table Z#ISNA_EXCH_BUFFER
  add constraint PK_Z#ISNA_EXCH_BUFFER_ID primary key (ID);

CREATE OR REPLACE TRIGGER Z#ISNA_EXCH_BUFFER_BIUDR
	BEFORE INSERT OR UPDATE OR DELETE ON Z#ISNA_EXCH_BUFFER
	FOR EACH ROW 
BEGIN 
	IF inserting THEN 
		SELECT IBS_ID_SEQ.nextval, UID, 1 
			INTO :NEW.id, :NEW.SU, :NEW.SN FROM dual;
	ELSIF updating THEN
		SELECT UID, :OLD.SN+1 INTO :NEW.SU, :NEW.SN FROM dual;
	END IF;
END;


INSERT INTO Z#ISNA_EXCH_BUFFER(c_inout,c_data,c_date_msg)
VALUES ('I',utl_raw.cast_to_raw('ПРивет мир'), sysdate);


SELECT * FROM Z#ISNA_EXCH_BUFFER;


create or replace package Z$ISNA_LIB_EXCHANGE is
	function PUTDATA_SYNC(PDATA IN blob) return blob;
	function PUTDATA_ASYNC(PDATA IN blob) return blob;
end Z$ISNA_LIB_EXCHANGE;


create or replace package BODY Z$ISNA_LIB_EXCHANGE IS
	function AddData(pContent IN blob)
		return NUMBER
	IS 
		l_id NUMBER;
	BEGIN 
		INSERT INTO  Z#ISNA_EXCH_BUFFER(c_inout,c_data,c_date_msg)
			VALUES ('I', pContent,sysdate) returning id INTO l_id;
		RETURN l_id;
	END;
	

	function PUTDATA_SYNC(pData IN blob) return blob
	IS 
		l_id NUMBER;
	BEGIN 
		l_id := AddData(pData);
		RETURN pData;
	END;
		
	function PUTDATA_ASYNC(pData IN blob) return blob
	IS 
	BEGIN 
		 raise_application_error( -20001, 'Тестовая ошибка');
	END;
end;



CREATE OR REPLACE PACKAGE rtl AS 
    
function  open(p_name varchar2 default NULL,
                   p_info varchar2 default NULL,
                   p_user_id pls_integer default NULL,
                   p_commit  boolean default true
                  ) return pls_integer;
END;

CREATE OR REPLACE PACKAGE BODY rtl AS 
    
	function  open(p_name varchar2 default NULL,
                   p_info varchar2 default NULL,
                   p_user_id pls_integer default NULL,
                   p_commit  boolean default true
                  )
	return pls_integer
	IS
	BEGIN
		RETURN 0;
	END;
 
END;




DECLARE
	lBlob Blob;
	lStr Raw(255);
	lAmount NUMBER;
BEGIN
	dbms_lob.createTemporary(lBlob, true);
	FOR i IN 1 .. 32000 LOOP
		lStr := utl_raw.cast_to_raw('Строка '||i||chr(10));
		lAmount := utl_raw.length(lStr);
		dbms_lob.writeAppend(lBlob, lAmount, lStr);
	END LOOP;
	lBlob := Z$ISNA_LIB_EXCHANGE.PUTDATA_SYNC(lBlob);
	dbms_lob.freeTemporary(lBlob);
END;
	
    
SELECT * FROM Z#ISNA_EXCH_BUFFER;    

SELECT id,dbms_lob.getLength(c_data) len FROM Z#ISNA_EXCH_BUFFER;