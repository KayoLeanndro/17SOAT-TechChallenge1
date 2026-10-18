package com.kap.mechanics_api.infra;

import java.time.Clock;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.kap.mechanics_api.core.cliente.gateway.ClienteGateway;
import com.kap.mechanics_api.core.itemestoque.gateway.ItemEstoqueGateway;
import com.kap.mechanics_api.core.orcamento.gateway.OrcamentoGateway;
import com.kap.mechanics_api.core.orcamento.usecase.AtualizarStatusDoOrcamentoUseCase;
import com.kap.mechanics_api.core.orcamento.usecase.ConsultarItensDoOrcamentoUseCase;
import com.kap.mechanics_api.core.orcamento.usecase.GerarOrcamentoUseCase;
import com.kap.mechanics_api.core.orcamento.usecase.IncluirItemNoOrcamentoUseCase;
import com.kap.mechanics_api.core.ordemservico.usecase.CriarOrdemServicoParaOrcamentoPendenteUseCase;
import com.kap.mechanics_api.core.ordemservico.usecase.FinalizarOrdemServicoPorOrcamentoUseCase;
import com.kap.mechanics_api.core.ordemservico.usecase.IniciarDiagnosticoPorOrcamentoUseCase;
import com.kap.mechanics_api.core.servico.gateway.ComponentesDeServicoGateway;
import com.kap.mechanics_api.core.servico.gateway.ServicoGateway;
import com.kap.mechanics_api.core.veiculo.gateway.VeiculoGateway;

@Configuration
public class OrcamentoUseCaseConfig {

    @Bean
    GerarOrcamentoUseCase gerarOrcamentoUseCase(OrcamentoGateway orcamentoGateway, ClienteGateway clienteGateway,
            VeiculoGateway veiculoGateway, ServicoGateway servicoGateway, ItemEstoqueGateway itemEstoqueGateway,
            ComponentesDeServicoGateway componentesGateway,
            CriarOrdemServicoParaOrcamentoPendenteUseCase criarOrdemServico, Clock clock) {
        return new GerarOrcamentoUseCase(orcamentoGateway, clienteGateway, veiculoGateway, servicoGateway,
                itemEstoqueGateway, componentesGateway, criarOrdemServico, clock);
    }

    @Bean
    IncluirItemNoOrcamentoUseCase incluirItemNoOrcamentoUseCase(OrcamentoGateway orcamentoGateway,
            ServicoGateway servicoGateway, ItemEstoqueGateway itemEstoqueGateway,
            ComponentesDeServicoGateway componentesGateway) {
        return new IncluirItemNoOrcamentoUseCase(orcamentoGateway, servicoGateway, itemEstoqueGateway,
                componentesGateway);
    }

    @Bean
    ConsultarItensDoOrcamentoUseCase consultarItensDoOrcamentoUseCase(OrcamentoGateway orcamentoGateway,
            ServicoGateway servicoGateway, ItemEstoqueGateway itemEstoqueGateway) {
        return new ConsultarItensDoOrcamentoUseCase(orcamentoGateway, servicoGateway, itemEstoqueGateway);
    }

    @Bean
    AtualizarStatusDoOrcamentoUseCase atualizarStatusDoOrcamentoUseCase(OrcamentoGateway orcamentoGateway,
            IniciarDiagnosticoPorOrcamentoUseCase iniciarDiagnostico,
            FinalizarOrdemServicoPorOrcamentoUseCase finalizarOrdemServico, Clock clock) {
        return new AtualizarStatusDoOrcamentoUseCase(orcamentoGateway, iniciarDiagnostico, finalizarOrdemServico,
                clock);
    }
}
