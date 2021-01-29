package com.com470.mockito1app;

//import org.junit.jupiter.api.Test;

import com.com470.mockito1app.controller.ExcepcionCuentaEnUso;
import com.com470.mockito1app.controller.ExcepcionUsuarioDesconocido;
import com.com470.mockito1app.controller.ICuenta;
import com.com470.mockito1app.controller.IRepositorioCuentas;
import com.com470.mockito1app.service.GestorLogin;
import org.junit.After;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import static org.mockito.Matchers.anyString;
import org.mockito.Mock;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


//import org.springframework.boot.test.context.SpringBootTest;

//@SpringBootTest
public class Mockito1appApplicationTests {
    
        GestorLogin login;
	IRepositorioCuentas repo; // collaborator
        @Mock
	ICuenta cuenta; // collaborator
        
        public Mockito1appApplicationTests() {
        }
        @Before
	public void InicializarTest() {
            repo = mock(IRepositorioCuentas.class);
            cuenta = mock(ICuenta.class);
            when(repo.buscar("pepe")).thenReturn(cuenta);
            login = new GestorLogin(repo);
	}
	@Test
	public void testAccesoConcedidoALaPrimera() {
		when(cuenta.claveCorrecta("1234")).thenReturn(true);
		login.acceder("pepe", "1234");
		verify(cuenta,times(1)).entrarCuenta(); 
		verify(cuenta,never()).bloquearCuenta(); 
	}
        @Test
	public void testAccesoDenegadoALaPrimera() {
		when(cuenta.claveCorrecta("1235")).thenReturn(false);
		login.acceder("pepe", "1235");
		verify(cuenta,never()).entrarCuenta();
		verify(cuenta,never()).bloquearCuenta();
		assertEquals(login.numFallos, 1);

	}
        @Test //(expected = ExcepcionUsuarioDesconocido.class)
	public void testUsuarioInconrrecto() {
		when(repo.buscar("bartolo")).thenReturn(null);		
		try {
			login.acceder("juan", "1234");
			fail("Se esperaba excepcion propia ExcepcionUsuarioDesconocido.");
		} catch (ExcepcionUsuarioDesconocido e) {
			verify(repo).buscar("juan");
			assertEquals(login.numFallos, 0);
		}
	}
        @Test
	public void testBloquearTrasTresIntentos() {
		when(cuenta.claveCorrecta("1235")).thenReturn(false);
                //GestorLogin login = new GestorLogin(repo);
		login.acceder("pepe", "1235");		
		login.acceder("pepe", "1235");
		login.acceder("pepe", "1235");
		verify(cuenta).bloquearCuenta();
		verify(cuenta,times(1)).bloquearCuenta();
		verify(cuenta, atLeastOnce()).bloquearCuenta();
		assertEquals(login.numFallos, 3);
	}
        @Test
	public void testAccederTrasDosIntentos() {
		when(cuenta.claveCorrecta("1234")).thenReturn(true);
		when(cuenta.claveCorrecta("1235")).thenReturn(false);
                //GestorLogin login = new GestorLogin(repo);
		login.acceder("pepe", "1235");
		login.acceder("pepe", "1235");
		login.acceder("pepe", "1234");	
		verify(cuenta,times(1)).entrarCuenta();
		verify(cuenta,never()).bloquearCuenta();
		assertEquals(login.numFallos, 2);
	}
        @Test
	public void testAccederTrasUnIntento() {
		when(cuenta.claveCorrecta("1234")).thenReturn(true);
		when(cuenta.claveCorrecta("1235")).thenReturn(false);
		//GestorLogin login = new GestorLogin(repo);
		login.acceder("pepe", "1235");
		login.acceder("pepe", "1234");
		verify(cuenta,times(1)).entrarCuenta(); //verifica que se invoca UNA vez
		verify(cuenta,never()).bloquearCuenta();
		assertEquals(login.numFallos, 1);
	}
        @Test
	public void testBloqueadarTrasCuatroIntentos() {
		when(cuenta.claveCorrecta("1235")).thenReturn(false);
		//GestorLogin login = new GestorLogin(repo);
		login.acceder("pepe", "1235");		
		login.acceder("pepe", "1235");
		login.acceder("pepe", "1235");
		login.acceder("pepe", "1235");

		verify(cuenta,times(1)).bloquearCuenta();
		assertEquals(login.numFallos, 4);	
	}
        @Test
	public void testAccesoDenegadoCuentaBloqueada() {
		when(cuenta.estaBloqueada()).thenReturn(true);
		login.acceder("pepe", anyString());
		verify(cuenta, never()).entrarCuenta();
	}
        @Test
	public void testSeDeniegaAccesoCuentaEnUso() {
		when(cuenta.claveCorrecta("1234")).thenReturn(true);		
		when(cuenta.estaEnUso()).thenReturn(true);
		try {
			login.acceder("pepe", "1234");
			fail("Se esperaba excepcion propia ExcepcionCuentaEnUso().");
		} catch (ExcepcionCuentaEnUso e) {
			assertEquals(login.numFallos, 0);	
		}
	}

}
