package br.com.ats.servlets;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.util.JSONPObject;

import br.com.ats.classes.entities.Usuario;
import br.com.ats.dao.DaoAutenticacaoRepository;
import br.com.ats.dao.DaoUsuarioRepository;

@WebServlet(urlPatterns = {"/principal/ServletLogin", "/ServletLogin"})
public class ServletLogin extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	
	private DaoAutenticacaoRepository repository = new DaoAutenticacaoRepository();
	private DaoUsuarioRepository repositoryUsuario = new DaoUsuarioRepository();
	
	private String urlPagPrincipal = "principal/principal.jsp";
	private String urlPagError = "error.jsp";
	private String urlPagInicial = "/index.jsp";
       
	/**
	 * Construtor padr�o
	 */
    public ServletLogin() {
    	
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String msg = "Usu�rio deslogado com sucesso!";
		String acao = request.getParameter("acao");
		
		/**
		 * Se logout for diferente de null e diferente de vazio e a acao for igual ao logout
		 * a sess�o � finalizada excluindo os dados do usu�rio logado e direcionando para a p�gina 
		 * autentica��o
		 */
		if (acao != null && !acao.isEmpty() && acao.equalsIgnoreCase("logout")) {
			request.setAttribute("msg", msg);
			request.getSession().invalidate();/*Invalida a sess�o, exclui os dados do usu�rio logado na sess�o*/
			request.getRequestDispatcher(urlPagInicial).forward(request, response);
		} else {
		
			doPost(request, response);
		}
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		try {
			
			String login = request.getParameter("login");
			String senha = request.getParameter("senha");
			String url = request.getParameter("url");
			
			/*validando os dados que est�o vindo da tela como par�metros*/
			if (login != null && !login.isEmpty() && senha != null && !senha.isEmpty()) {
				
				Usuario objetoUsuario = new Usuario();
				objetoUsuario.setLogin(login);
				objetoUsuario.setSenha(senha);
				
				/*Autenticando os par�metros no banco de dados*/
				if (repository.validarAutenticacao(objetoUsuario)) {
					
					objetoUsuario = repositoryUsuario.consultar(login);
					
					/*Pegando os atributos do objeto usu�rio e matendo o usu�rio logado na sess�o*/
					request.getSession().setAttribute("usuario", objetoUsuario.getLogin());
					request.getSession().setAttribute("perfil", objetoUsuario.getPerfil());
					
					if (url == null || url.equals("null")) {
						url = urlPagPrincipal;
					}
					
					/*Redireciona a p�gina ap�s a autentica��o est� correta*/
					request.getRequestDispatcher(url).forward(request, response);
					request.setAttribute("msg", "Usu�rio " + objetoUsuario.getNome() + " logado com sucesso!");
					
				} else {
					
					request.getRequestDispatcher(urlPagInicial).forward(request, response);
					request.setAttribute("msg", "Login e/ou senha est�o incorretos!");
				}
				
			} else {
				
				request.getRequestDispatcher(urlPagInicial).forward(request, response);
				request.setAttribute("msg", "Informe o login e a senha!");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			request.getRequestDispatcher(urlPagError).forward(request, response);
			request.setAttribute("msg", e.getMessage());
		}
	}

}
