CREATE PROCEDURE INSERT_BITACORA_RIPLEY 
@tipo varchar(2000),
@datos_in varchar(2000),
@datos_out varchar(2000),
@ip varchar(2000),
@usuario varchar(2000)
AS
insert into BITACORA_RIPLEY_AUTH_SMS_PIN4 (tipo,datos_in,datos_out,ip,usuario)
values(@tipo,@datos_in,@datos_out,@ip,@usuario)