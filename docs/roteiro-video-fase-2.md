# Roteiro de gravação — Kayo | Tech Challenge Fase 2

Duração prevista: 13 a 14 minutos, contando as demonstrações. Os trechos em citação são falas sugeridas; as instruções entre colchetes não precisam ser lidas. Adapte as palavras à sua maneira de falar. Preencha quantidades e resultados somente com o que aparecer na tela.

## Antes de apertar gravar

Deixe abertas estas abas, nesta ordem:

1. Seu desenho de arquitetura.
2. README e uma execução recente, concluída com sucesso, do GitHub Actions na main.
3. Console AWS na região us-east-1: ECR, EKS e EC2 > Load Balancers.
4. Swagger público da aplicação.

No EKS, confirme antecipadamente que seu usuário consegue visualizar os recursos Kubernetes no namespace default: Deployment, pods, Job e HPA. Prepare um usuário de demonstração autorizado a consultar clientes e serviços. Teste o login antes, sem assumir que credenciais locais funcionam na AWS.

Prepare e ensaie a carga de CPU limitada a 120 segundos. Inicie-a somente na parte de escala; você pode pausar a gravação para iniciar e depois retomar, explicando o procedimento. Não altere manualmente a quantidade de réplicas. Antes do teste, o HPA precisa apresentar métricas e estar abaixo do limite de seis pods.

Oculte senha e JWT na edição. Não abra valores dos segredos. Pode cortar tempos de espera, mantendo a ordem e os resultados reais. Garanta que o vídeo final tenha até 15 minutos.

Seu desenho ainda precisa refletir o fluxo implementado: Secrets Manager → External Secrets Operator → Secret; HPA → Deployment → pods; ALB → IPs dos pods, com Ingress/controller configurando o acesso. Se o desenho ainda mostrar ClusterIP como salto obrigatório ou HPA escalando nós, corrija antes de gravar. O roteiro abaixo explica a implementação real.

## 1. Apresentação — 0:00 a 0:45

[Comece com o repositório ou o desenho aberto, em um zoom legível.]

> Olá, pessoal. Meu nome é Kayo e vou apresentar a nossa solução para a segunda fase do Tech Challenge.
>
> O projeto é uma API para gestão de uma oficina mecânica. Ela reúne funcionalidades como cadastro de clientes e veículos, serviços e ordens de serviço.
>
> Nesta fase, o foco foi levar essa aplicação para um ambiente Kubernetes na AWS e automatizar o caminho entre uma alteração no código e a aplicação atualizada. Vou começar pela arquitetura e depois mostrar esse fluxo funcionando, desde a pipeline até o acesso às APIs e a escala dos pods.

## 2. Explicando seu desenho — 0:45 a 3:00

[Abra o desenho completo. Primeiro aponte para a parte de CI/CD à esquerda. Depois amplie o ambiente AWS à direita. Evite movimentar o mouse o tempo todo.]

> Eu dividi o desenho em duas partes. À esquerda está a automação com GitHub Actions. À direita está o ambiente AWS onde a aplicação executa.
>
> Começando pela esquerda: quando fazemos um push na main, a pipeline executa os testes, constrói a imagem Docker e verifica as migrations em um banco temporário. Só depois dessa validação a imagem segue para publicação no ECR e para o deploy no EKS. Daqui a pouco eu vou abrir uma execução para mostrar essas etapas.

[Aponte para cliente, ALB e pods.]

> Agora, olhando para o acesso à aplicação: o cliente pode usar o Swagger, o Postman ou outro consumidor HTTP. A requisição entra pelo endereço público do Application Load Balancer e é encaminhada para um dos pods disponíveis da API.
>
> Nesse ambiente, o ALB utiliza os IPs dos pods como destino, na porta 8080. O Ingress e o AWS Load Balancer Controller fazem a configuração desse acesso, usando o Service como referência para identificar a aplicação.
>
> Cada pod tem seu próprio IP. Então todos podem executar a API na porta 8080, inclusive quando estão no mesmo nó, sem conflito de portas.

[Aponte para RDS e para o Job.]

> Os dados ficam no PostgreSQL do RDS, que está sem acesso público. Além dos pods da API, temos esse Job Kubernetes para as migrations. Ele prepara ou valida a estrutura do banco antes da atualização da aplicação.
>
> O Job usa a mesma imagem que será entregue para a API, mas inicia em um modo que executa somente o Flyway e encerra. Assim, não precisamos iniciar um servidor HTTP para atualizar o banco.

[Aponte para Secrets Manager, operador, Secret e ConfigMap.]

> As configurações ficam separadas dos valores sensíveis. O ConfigMap fornece configurações como o endereço do banco. A senha do banco e a chave de assinatura do JWT ficam no Secrets Manager e são sincronizadas para um Secret Kubernetes pelo External Secrets Operator.
>
> O acesso desse operador à AWS utiliza Pod Identity. Dessa forma, não colocamos uma chave AWS estática dentro da imagem da aplicação.

[Aponte para HPA, Deployment e grupo de nós.]

> Aqui fica o controle de escala. O Metrics Server disponibiliza as métricas, e o HPA utiliza CPU e memória para ajustar o Deployment entre dois e seis pods. Esse número é a quantidade de réplicas da aplicação. Os nós EC2 são as máquinas que recebem esses pods; o HPA não aumenta a quantidade dessas máquinas.

[Aponte para S3 e para o título do ambiente.]

> A infraestrutura AWS está definida em Terraform, com o state no S3. Helm instala os componentes do cluster, e os manifestos Kubernetes definem os recursos da aplicação. Então cada ferramenta tem uma responsabilidade nesse ambiente.

[Mostre brevemente o diagrama da aplicação no README. Se ele não estiver no seu desenho principal, use uma segunda aba.]

> Além da infraestrutura, aqui mostramos a organização da aplicação. No contexto Cliente, por exemplo, temos a entrada HTTP pelos controllers, os casos de uso e o domínio, e os adapters que fazem a integração com a persistência. Isso ajuda a separar as regras de negócio dos detalhes de integração.

## 3. Mostrando a entrega no GitHub — 3:00 a 5:30

[Abra GitHub > Actions > execução recente da main. Deixe visível o commit/SHA e os três jobs. Não é necessário esperar uma pipeline inteira durante o vídeo; explique se estiver mostrando uma execução já concluída.]

> Com essa visão da arquitetura, vou mostrar agora uma entrega real. Esta execução corresponde a este commit da main. Estou mostrando uma execução já concluída para conseguirmos acompanhar os resultados sem esperar todo o processamento durante a apresentação.
>
> A pipeline está dividida em três jobs: testar e validar a imagem, publicar a imagem e implantar a aplicação no EKS.

[Abra o primeiro job. Mostre os testes Maven e depois a validação das migrations.]

> No primeiro job, executamos os testes com Maven e geramos o relatório de cobertura. Depois construímos a imagem Docker.
>
> Antes de publicar, também testamos essa imagem com um PostgreSQL temporário. Aplicamos as migrations, executamos novamente para verificar que não são reaplicadas e confirmamos que a API inicia com o schema esperado. A imagem validada é guardada como artifact para a próxima etapa.

[Abra o segundo job. Mostre autenticação, resultado do Terraform e publicação.]

> Aqui a pipeline assume uma role AWS usando OIDC, com credenciais temporárias. Em seguida executa o planejamento e a aplicação do Terraform e publica a imagem validada no ECR.
>
> A tag é o SHA do commit, o que permite relacionar a versão que está rodando com o código dessa entrega.

[Se o Terraform mostrar nenhuma mudança, diga:]

> Nesta execução, a infraestrutura já estava no estado esperado, então o Terraform não precisou alterar recursos.

[Abra o terceiro job e expanda a etapa de migrations e deploy. Localize os logs antes de gravar para evitar procurar por muito tempo.]

> Antes de trocar os pods, essa etapa cria o Job de migrations no EKS. Aqui aparece o resultado do Flyway.

[Escolha somente a fala compatível com o log.]

> Neste caso, o banco já estava atualizado e a verificação terminou com sucesso.

[Ou, se houve migrations novas:]

> Neste caso, estas migrations estavam pendentes e foram aplicadas com sucesso.

> Somente depois desse sucesso a pipeline atualiza o Deployment e aguarda o rollout. Se o Job falhar, essa atualização é bloqueada. Ao final, ela verifica se o Swagger e o OpenAPI estão acessíveis pelo endereço público.

[Mostre a conclusão do rollout. Não afirme que existe rollback automático de migrations: esse mecanismo não está implementado.]

## 4. Confirmando no Console AWS — 5:30 a 7:00

[Abra ECR > mechanics-api > Images.]

> Agora vou conferir o resultado na AWS. No ECR, esta tag corresponde ao commit que acabamos de ver no GitHub. É a imagem publicada por aquela execução.

[Abra EKS > mechanics-api-dev > recursos Kubernetes, namespace default. Mostre o Deployment mechanics-api e os pods.]

> No EKS, aqui está o Deployment da aplicação. Podemos conferir a imagem utilizada e as réplicas disponíveis. Estes são os pods que estão executando a API neste momento.

[Se o Job recente ainda existir, mostre sua conclusão. Se tiver sido removido, use os logs já mostrados no GitHub; ele tem limpeza automática de 24 horas.]

> Aqui também conseguimos acompanhar o Job de migrations e seu resultado.

[Use essa frase somente se o Job estiver visível.]

[Abra EC2 > Load Balancers > ALB da aplicação. Mostre listener e target group associado.]

> Este é o ALB que recebe o acesso público. O listener está na porta 80, usando HTTP, e aqui no target group aparecem os destinos saudáveis. São os pods que estão aptos a receber as requisições.

## 5. Consumindo a API no Swagger — 7:00 a 9:00

[Abra o Swagger pelo endereço público do ALB. Remova qualquer autorização já configurada antes do teste sem token.]

> Agora vou usar esse acesso público para consumir a API pelo Swagger. Primeiro vou fazer uma consulta sem autenticação, para mostrar a proteção da rota.

[Execute GET /api/cliente sem JWT. Mostre o HTTP 401 esperado.]

> A consulta foi bloqueada porque ainda não informamos um token de autenticação.

[Abra POST /api/auth/login. Use o usuário preparado; oculte senha e token na edição.]

> Vou autenticar com um usuário de demonstração que tem permissão para essas consultas. O login retorna um JWT, que será enviado nas próximas requisições.

[Execute login, copie o token e abra Authorize > bearerAuth. Cole somente o JWT.]

> Com a autorização configurada, vou repetir a mesma consulta.

[Execute GET /api/cliente e mostre o HTTP 200 e o JSON.]

> Agora a consulta retornou com sucesso. Esses dados são atendidos pela aplicação no EKS, com persistência no RDS.

[Execute GET /api/servico e mostre o resultado.]

> Aqui também consigo consultar os serviços. Com isso mostramos o acesso externo à aplicação e o uso da autenticação nas rotas de negócio.

[Não leia tokens nem dados pessoais. Se a resposta divergir do ensaio, investigue antes de gravar a versão final.]

## 6. Demonstrando o HPA — 9:00 a 12:30

[Volte ao EKS. Mostre o HPA e o Deployment/pods da aplicação. Registre a contagem inicial antes da carga.]

> Para finalizar a parte prática, vou mostrar a escala automática. Neste momento temos [N] réplicas da aplicação.
>
> O HPA está configurado com mínimo de dois e máximo de seis pods. As metas são 70% de CPU e 80% de memória, em relação aos requests dos containers. O número mínimo não significa que a aplicação vai ficar sempre com exatamente dois pods, porque o consumo de memória também participa dessa decisão.
>
> Para esta demonstração, vou iniciar uma carga sintética de CPU por dois minutos em um dos containers. A ideia é observar a reação do HPA ao consumo de recursos. Essa carga não representa um teste de volume de requisições de negócio.

[Pause para iniciar a carga fora da gravação ou coordene a execução previamente preparada. Retome ainda na tela do EKS.]

> A carga foi iniciada. Vou atualizar a tela para acompanhar as métricas e a quantidade desejada de réplicas.

[Mostre métricas/estado do HPA disponíveis na interface e depois Deployment e pods. Corte a espera, se necessário. Não altere manualmente a escala.]

[Quando o aumento realmente aparecer:]

> Aqui já vemos a mudança: saímos de [N] para [M] réplicas desejadas. Estes novos pods estão sendo criados pelo Deployment em resposta ao HPA.

[Atualize até os novos pods estarem prontos.]

> Agora temos [M] réplicas disponíveis. Eu não executei uma alteração manual na quantidade de pods; essa decisão foi tomada pelo HPA a partir das métricas.
>
> Quando a carga termina, a redução não precisa acontecer imediatamente. Existe uma janela de estabilização de cinco minutos, e o consumo de memória também pode manter mais réplicas ativas. Para manter a apresentação dentro do tempo, não vou esperar todo esse processo de redução.

[Se houver tempo, volte ao Swagger e repita uma consulta para mostrar a aplicação respondendo após a escala.]

> Depois do aumento das réplicas, a aplicação continua respondendo pelo mesmo endereço público. O cliente não precisa conhecer os IPs dos novos pods.

[Se a interface não mostrar métricas/HPA, resolva isso no ensaio. Mostrar apenas mais pods, sem evidências do HPA, não comprova a causa da escala. Não prometa um número exato de réplicas nem resultado antes de observá-lo.]

## 7. Encerramento — 12:30 a 13:30

[Volte ao README ou ao desenho.]

> Então, esse foi o fluxo que implementamos nesta fase: a alteração entra pelo GitHub, passa pelos testes, gera uma imagem validada e chega ao EKS depois da execução das migrations.
>
> Também mostramos a aplicação acessível pelo ALB, o consumo das APIs com JWT e o HPA aumentando as réplicas conforme a utilização de recursos.
>
> No repositório estão o código, o Dockerfile, a configuração de execução local, os manifestos Kubernetes, o Terraform e a pipeline, junto com as instruções para reproduzir o ambiente.
>
> O ambiente apresentado é de desenvolvimento. Como próximos passos, temos HTTPS e observabilidade centralizada. Os links do repositório, da documentação das APIs e deste vídeo acompanham a entrega.
>
> Eu sou o Kayo. Obrigado por acompanhar a apresentação.

## Conferência antes de publicar

- Até 15 minutos, publicado no YouTube ou Vimeo como público ou não listado.
- Desenho legível e coerente com a implementação.
- Commit e pipeline identificáveis, resultado das migrations e rollout visíveis.
- Consumo autenticado mostrado sem senha/JWT legíveis.
- Aumento real das réplicas pelo HPA e novos pods prontos.
- Link do vídeo incluído no README e no PDF de entrega.
- PDF com link do repositório e desenho de arquitetura.
- Compartilhamento do repositório com soat-architecture conferido.
