package br.org.apae.atendimento;

import br.org.apae.atendimento.integration.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.beans.factory.annotation.Autowired;
import static org.assertj.core.api.Assertions.assertThat;

class AtendimentoApplicationTests extends AbstractIntegrationTest {

	@Autowired
	private ApplicationContext applicationContext;

	@Test
	void contextLoads() {
		assertThat(applicationContext).isNotNull();
	}
}