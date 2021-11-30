create table BITACORA_RIPLEY_AUTH_SMS_PIN4
(
id int identity primary key,
tipo varchar(2000),
datos_in varchar(2000),
datos_out varchar(2000),
fecha datetime default getdate(),
ip varchar(2000),
usuario varchar(2000)
)