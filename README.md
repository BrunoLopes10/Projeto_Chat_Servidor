Fiz um chat em /jar onde consigo conversar com minha própria maquina por exemplo do terminal para o cmd, como se fosse um WhatsApp, utilizando o docker

Para rodar o servidor, digite no terminal ou no cmd da pasta que se econtra o código:
docker run -it srv

Para rodar o cliente, digite no terminal ou no cmd da pasta que se encontra o código:
docker run -it <nome-img> Java Cliente 172.17.0.2 8080
                   /\
                  cli
