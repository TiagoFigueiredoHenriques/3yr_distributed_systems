
### Instruções ###

1º Compilar os ficheiros .jar verificando que se encontram todos na package com.example.servingwebcontent
	SD_Project
	UrlInfo	
	RemoteInterface
	RemoteInterfaceSB
	URL_Queue
	Storage_Barrel
	Downloader
	RMI_Search_Module
	ServingWebContentAppliction
	

2º Correr os ficheiros por esta ordem
	URL_Queue
	Storage_Barrel
	Downloader
	RMI_Search_Module
	ServingWebContentAppliction

3º No caso de não existir o ficheiro de objetos com nome "queue.ser"
	No browser com o link "http://localhost:8080/":
		Fazer registo/login de um utilizador
		Fazer send_url para enviar um url para a URL_Queue
		Utilizar as funcoes desponibilizadas no programa

4º Caso o 3º passo não tenha sido necessário basta:
	Fazer registo de um utilizador
	Fazer login desse utilizador
	Utilizar as funcoes desponibilizadas no programa