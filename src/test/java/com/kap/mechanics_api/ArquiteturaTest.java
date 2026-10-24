package com.kap.mechanics_api;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

// Regra de Dependência da Clean Architecture: o core não pode conhecer as camadas externas.
// Ativa apenas para o core, que hoje contém Cliente, Veiculo, Servico, ItemEstoque, MovimentacaoEstoque,
// Orcamento, OrdemServico e Usuario. Auth continua na camada de service.
// Regras para adapter/infra serão ligadas conforme os demais contextos forem migrados.
@AnalyzeClasses(packages = "com.kap.mechanics_api", importOptions = ImportOption.DoNotIncludeTests.class)
class ArquiteturaTest {

    @ArchTest
    static final ArchRule coreNaoDependeDeFrameworks = noClasses()
            .that().resideInAPackage("..core..")
            .should().dependOnClassesThat()
            .resideInAnyPackage("org.springframework..", "jakarta..", "org.hibernate..", "org.mapstruct..");

    @ArchTest
    static final ArchRule coreNaoDependeDasCamadasExternas = noClasses()
            .that().resideInAPackage("..core..")
            .should().dependOnClassesThat()
            .resideInAnyPackage(
                    // prefixo completo: "..domain.." casaria também com core.cliente.domain
                    "com.kap.mechanics_api.adapter..", "com.kap.mechanics_api.infra..",
                    "com.kap.mechanics_api.controller..", "com.kap.mechanics_api.service..",
                    "com.kap.mechanics_api.repository..", "com.kap.mechanics_api.mapper..",
                    "com.kap.mechanics_api.dto..", "com.kap.mechanics_api.domain..",
                    "com.kap.mechanics_api.exception..", "com.kap.mechanics_api.config..",
                    "com.kap.mechanics_api.security..");
}
