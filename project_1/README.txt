
### Instruções ###

1º Compilar os ficheiros .jar verificando que se encontram todos na package sd_project
	SD_Project
	UrlInfo	
	RemoteInterface
	RemoteInterfaceSB
	URL_Queue
	Storage_Barrel
	Downloader
	RMI_Search_Module
	RMI_Client

2º Correr os ficheiros por esta ordem
	URL_Queue
	Storage_Barrel
	Downloader
	RMI_Search_Module
	RMI_Client

3º No caso de não existir o ficheiro de objetos com nome "queue.ser"
	No ficheiro RMI_Client:
		Fazer registo de um utilizador
		Fazer login desse utilizador
		Fazer send_url para enviar um url para a URL_Queue
		Utilizar as funcoes desponibilizadas no programa RMI_Client

4º Caso o 3º passo não tenha sido necessário basta:
	Fazer registo de um utilizador
	Fazer login desse utilizador
	Utilizar as funcoes desponibilizadas no programa RMI_Client