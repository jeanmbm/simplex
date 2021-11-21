import java.util.Scanner;

public class simplex {
	
	static double[][] tabela;
	static int linha;
	static int coluna;
	static String[] linhaVariaveis;
	static String[] colunaVariaveis;
	

    public static void main(String[] args) {
    	
    	preenchimentoInicial();
    	
        boolean verificar = true;
        int count = 0;
        
        while (verificar == true) {
        	novaLinhaPivo();
            
            calculoNovasLinhas();
            count++;
        	
            System.out.println();
            System.out.println("===========================================================");
            System.out.println("Tabela "+ count +" com os novos valores");
            System.out.println();
            
            exibirTabela();
            
            verificar = analisarSetOtimo();
        }
        
        exibirResposta();
        
    }
    
    public static void preenchimentoInicial() {
    	
    	int restricoes, variaveis;
    	double[][] restricao;
    	boolean maximizacao;
    	Scanner input = new Scanner(System.in); 
    	
    	System.out.print("Digite a quantidade de restrições: ");
    	restricoes = input.nextInt();
    	System.out.print("Digite a quantidade de variáveis de decisão: ");
    	variaveis = input.nextInt();
    	
    	restricao = new double[restricoes][variaveis + 1];
    	  	
    	System.out.println("\nDigite o tipo da função objetivo: 1 - Maximizar, 2 - Minimizar");
    	int maxmin = input.nextInt();
    	if (maxmin == 1) {
			maximizacao = true;
		} else {
			maximizacao = false;
		}
    	
    	double[] funcaoObjetivo = new double[variaveis];
		
		System.out.println("\nFUNÇÃO OBJETIVO ");
		for (int i = 0; i < funcaoObjetivo.length; i++) {
			System.out.print("Digite o valor de X" + (i + 1) + " da função objetivo: ");
    		funcaoObjetivo[i] = input.nextDouble();
		}
    	
    	for (int i = 0; i < restricoes; i++) {
    		System.out.println();
			System.out.println("RESTRIÇÃO " + (i + 1) + ": ");
			for (int j = 0; j < variaveis; j++) {
				System.out.print("Digite o coeficiente de X" + (j + 1) + ": ");
				restricao[i][j] = input.nextDouble();
			}
			System.out.print("Digite o valor do termo independente: ");
			restricao[i][variaveis] = input.nextDouble();
		}
    	
    	preencherVariaveis(variaveis, restricoes);
    	
		montarMatriz(variaveis, restricoes, restricao, funcaoObjetivo, maximizacao);
    }
    
    // monta a matriz 
    public static double[][] montarMatriz(int numeroVariaveisDecisao, int numeroRestricao, double[][] matrizRestricao, double[] FuncaoObjetiva, boolean maximizacao) {
		
    	linha = numeroRestricao + 1;
		coluna = numeroVariaveisDecisao + numeroRestricao + 2;
		tabela = new double[linha][coluna];

		// Preencher tabela com Zero
		for (int i = 0; i < linha; i++) {
			for (int j = 0; j < coluna; j++)
				tabela[i][j] = 0;
		}

		// Preencher primeira linha com valores da função objetiva
		tabela[0][0] = 1;
		for (int i = 1; i < numeroVariaveisDecisao + 1; i++) {
//			if (maximizacao) {
				tabela[0][i] = FuncaoObjetiva[i - 1] * (-1);
//			} else {
//				tabela[0][i] = FuncaoObjetiva[i - 1];
//			}
		}


		for (int i = 1; i < linha; i++) {
			tabela[i][coluna - 1] = matrizRestricao[i - 1][numeroVariaveisDecisao];      // preencher a ultima coluna com os
																						 // valores dos termos independentes
			
			// Insere os coeficientes das variaveis de folgas
			for (int j = numeroVariaveisDecisao + 1; j < coluna - 1; j++) {  
				if (j == numeroVariaveisDecisao + i)
				tabela[i][j] = 1;
			}
			
			// Insere os coeficientes das variaveis de decisão
			for (int j = 0; j < numeroVariaveisDecisao; j++) {
				tabela[i][j + 1] = matrizRestricao[i - 1][j];   			
			}
		}
		
		exibirTabela();

		return tabela;
	}
    
    // Determina o maior valor em modulo, define a coluna da NLP
    public static int colunaPivo() {
        double maior = 0;
        int posicao = 0;
        
        for (int i = 0; i < coluna - 1; i++) {
        	if (tabela[0][i] < 0) {
        		double valorAbsoluto = Math.abs(tabela[0][i]);
        		if ( valorAbsoluto > maior) {
        			maior = valorAbsoluto;
        			posicao = i;
        		}
        	}
        }
        
        return posicao;
    }

    // Determina o menor valor da divisão, define a linha da NLP
    public static int linhaPivo() {
        double menor = 99999999999999999999.0;
        int posicao = 0;
        int colunaPivo = colunaPivo();
        
        for (int i = 1; i < linha; i++) {
            if (tabela[i][colunaPivo] != 0) {
                double divisao = tabela[i][coluna - 1] / tabela[i][colunaPivo];
                if (divisao < menor && divisao > 0) {
                    menor = divisao;
                    posicao = i;
                }
            }
        }
        
        return posicao;
    }
    
    // muda os valores das variaveis 
    public static void mudarVariavelBase() {
		int colunaPivo = colunaPivo();	
		int linhaPivo = linhaPivo();	
		
		System.out.println("A variável: " + colunaVariaveis[linhaPivo] + 
							" da posição: " + (linhaPivo + 1) + " vai mudar pela variável: " + linhaVariaveis[colunaPivo] +
							" da posição: " + (colunaPivo + 1));
		
		colunaVariaveis[linhaPivo] = linhaVariaveis[colunaPivo];
		
		System.out.println("A variável da posição: " + (linhaPivo + 1) + " agora é: "
							+ colunaVariaveis[linhaPivo]);
		
	}
    
    // Divide a NLP pelo elemento pivô
    public static void novaLinhaPivo() {
		
    	int colunaPivo = colunaPivo();
    	int linhaPivo = linhaPivo();
    	
    	double elementoPivo = tabela[linhaPivo][colunaPivo];
    	
        for (int i = 0; i < coluna; i++) {
        	tabela[linhaPivo][i] = tabela[linhaPivo][i] / elementoPivo;
        }
        
        System.out.println("");
    	System.out.println("Linha posição: "+ (linhaPivo + 1) +" será a nova linha pivo \n");
    	System.out.println("Coluna posição: "+ (colunaPivo + 1) +" será a nova coluna da linha pivo \n");
    	
	    mudarVariavelBase();
    }
    
    // Armazena a coluna pivô para o calculo das novas linhas não pivô.
    public static double[] pegarColunaPivo() {

        double colunaPivo[] = new double[linha];
        int coluna = colunaPivo();
        
        for (int i = 0; i < linha; i++) {
            colunaPivo[i] = tabela[i][coluna];
        }
        
        return colunaPivo;
    }
    
    // Armazena a linha da NLP para o calculo das novas linhas não pivô.
    public static double[] pegarLinhaPivo() {
    	
    	double linhaPivo[] = new double[coluna];
    	int linha = linhaPivo();
    	
    	for (int i = 0; i < linhaPivo.length; i++) {
    		linhaPivo[i] = tabela[linha][i];
		}
    	
    	return linhaPivo;
    }
    
    // armazena valores da tabela para não sobrescrever os valores direto 
    public static double[][] armazenarValores() {
    	
    	double[][] valoresTabela = new double[linha][coluna];
    	
    	for (int i = 0; i < linha; i++) {
			for (int j = 0; j < coluna; j++) {
				valoresTabela[i][j] = tabela[i][j];
			}
		}
    	
		return valoresTabela;   	
    }
    
    // Novas valores das linhas não pivô.
    public static void calculoNovasLinhas() {
    	
    	int posicaoLinhaPivo = linhaPivo();
    	double colunaPivo[] = pegarColunaPivo();
    	double linhaPivo[] = pegarLinhaPivo();
    	double[][] valores = armazenarValores();
    	
    	for (int i = 0; i < linha; i++) {
    		for (int j = 0; j < coluna; j++) {
    			if (i != posicaoLinhaPivo) {
    				tabela[i][j] = valores[i][j] - (colunaPivo[i] * linhaPivo[j]);
				}
    		}
		}
    }
    
    // avalia se há valores negativos na linha Z
    public static boolean analisarSetOtimo() {
    	boolean isPassed = true;
    	int count = 0;
    	
    	for (int i = 0; i < coluna; i++) {
    		if (tabela[0][i] < 0) {
    			count++;
			}
		}
    	
    	if (count == 0) {
    		isPassed = false;
		}
    	
    	return isPassed;
    }
	
    // metodo que exibe a resposta final com os valores para as variaveis
	public static void exibirResposta() {
		
		System.out.println("\n\n======| RESULTADO |======");
		for (int i = 0; i < colunaVariaveis.length; i++) {
			System.out.printf("|\t %s = %.2f \t|\n", colunaVariaveis[i], tabela[i][coluna - 1]);
		}
		System.out.println("|_______________________|");
		
	}
    
	// preenche vetores com as variaveis para exibição e mudança de base
    public static void preencherVariaveis(int variaveis, int restricoes) {
    	
    	linhaVariaveis = new String[variaveis + restricoes + 2];
    	colunaVariaveis = new String[restricoes +1];
    	
    	// preenche o vetor de linhas com as variaveis de decisão
    	for (int i = 0; i <= (linhaVariaveis.length - restricoes - 2); i++) {
    		if(i == 0) {
    			linhaVariaveis[i] = "Z";
    		} else {
    			linhaVariaveis[i] = "X" + String.valueOf(i);
    		}
		}
    	
    	// preenche o vetor de linhas com as variaveis de folga
    	int count = 1;
    	for (int i = variaveis; i < linhaVariaveis.length - 2; i++) {
    		linhaVariaveis[i + 1] = "Y" + String.valueOf(count++);
		}
    	
    	linhaVariaveis[linhaVariaveis.length - 1] = "b";
    	
    	// preenche o vetor de coluna com as variaveis de folga (variaveis base)
    	for (int i = 0; i <= restricoes; i++) {
    		if(i == 0) {
    			colunaVariaveis[i] = "Z";
    		} else {
    			colunaVariaveis[i] = "Y" + String.valueOf(i);
    		}	
		}
    }
    
    // metodo que monta a tabela no console
    public static void exibirTabela() {
    	
    	System.out.print("\t");
    	for (int i = 0; i < coluna; i++) {
			System.out.printf("|\t %s \t", linhaVariaveis[i]);
		}
    	
    	System.out.println();
    	for (int i = 0; i < linha; i++) {
            for (int j = 0; j < coluna; j++) {
            	if (j == 0) System.out.printf("%s \t", colunaVariaveis[i]);
            	
            	System.out.printf("|\t %.2f \t", tabela[i][j]);
            }
            System.out.println();
  		}
    }
}
