package br.org.apae.atendimento.config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

import br.org.apae.atendimento.entities.TipoArquivo;
import br.org.apae.atendimento.repositories.TipoArquivoRepository;


@Configuration
public class TesteJpaConfig implements CommandLineRunner{
    
        @Autowired
        private TipoArquivoRepository tipoArquivoRepository;

        public void run(String... args){
             TipoArquivo tipoArquivoAnexo = new TipoArquivo();
             tipoArquivoAnexo.setId(1L);
             tipoArquivoAnexo.setTipo("Anexo");
             tipoArquivoRepository.save(tipoArquivoAnexo);


             TipoArquivo tipoArquivoRelatorio = new TipoArquivo();
             tipoArquivoRelatorio.setId(2L);
             tipoArquivoRelatorio.setTipo("Relatorio");
             tipoArquivoRepository.save(tipoArquivoRelatorio); 
        }
}
