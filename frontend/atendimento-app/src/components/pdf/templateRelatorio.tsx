import {
  Document,
  Page,
  Text,
  View,
  Image,
} from "@react-pdf/renderer";
import { styles } from "./styles";
import { formatarDataExtenso } from "@/lib/utils";

interface TemplateRelatorioProps {

  paciente: {
    nome: string;
    dataNascimento: string;
    endereco: string;
    responsavel?: string;
  };

  profissional: {
    nome: string;
    crp: string;
  };

  titulo: string;
  descricao: string;
}

const calcularIdade = (dataNascimento: string): number => {
  const [dia, mes, ano] = dataNascimento.split("/").map(Number);

  const nascimento = new Date(ano, mes - 1, dia);
  const hoje = new Date();

  let idade = hoje.getFullYear() - nascimento.getFullYear();

  const mesAtual = hoje.getMonth();
  const mesNascimento = nascimento.getMonth();

  const diaAtual = hoje.getDate();
  const diaNascimento = nascimento.getDate();

  if (
    mesAtual < mesNascimento ||
    (mesAtual === mesNascimento && diaAtual < diaNascimento)
  ) {
    idade--;
  }

  return idade;
};

export const TemplateRelatorio = ({
  paciente,
  profissional,
  titulo,
  descricao,
}: TemplateRelatorioProps) => {

  const idade = calcularIdade(paciente.dataNascimento);

  const dataGeracao = formatarDataExtenso(new Date());

  return (
    <Document>
        <Page size={"A4"} style={styles.page}>

          <View style={styles.header}>
            <Image src="/logo-30anos.png" style={styles.logo}/>

            <View style={styles.headerInfo}>
              <Text style={styles.apae}>APAE</Text>
              <Text style={styles.apae}>ASSOCIAÇÃO DE PAIS E AMIGOS DOS EXCEPCIONAIS</Text>
              <Text>CNPJ: 01.180.414/0001-02</Text>
              <Text>Rua Santo Antônio, 491 - Esperança - PB - CEP.: 58.135-000 </Text>
              <Text>FUNDADA EM 14 DE OUTUBRO DE 1995 E RECONHECIDA DE UTILIDADE PÚBLICA</Text>
            </View>
          </View>

          <Text style={styles.title}>SETOR DE PSICOLOGIA</Text>

          <View style={styles.section}>
            <Text style={styles.field}>
              Nome: {paciente.nome}
            </Text>

            <Text style={styles.field}>
              Data de Nascimento: {paciente.dataNascimento} – Idade: {idade} anos
            </Text>

            <Text style={styles.field}>
              Endereço: {paciente.endereco}
            </Text>

            {paciente.responsavel && (
              <Text style={styles.field}>
                Responsável: {paciente.responsavel}
              </Text>
            )}

            <Text style={styles.field}>
              Profissional Responsável: {profissional.nome}
            </Text>

            <Text style={styles.field}>
              CRP: {profissional.crp}
            </Text>

          </View>
          
          <Text style={styles.title}>{titulo}</Text>

          <Text style={styles.description}>{descricao}</Text>

          <View style={styles.footer}>
            <Text>Esperança - PB, {dataGeracao}</Text>
          </View>

        </Page>
    </Document>
  )
};
