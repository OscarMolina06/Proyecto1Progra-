/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Entidad;

public class Direccion {

    private String codigoElectoral;
    private String provincia;
    private String canton;
    private String distrito;

    public Direccion() {
    }

    public Direccion(String codigoElectoral, String provincia, String canton, String distrito) {
        this.codigoElectoral = codigoElectoral;
        this.provincia = provincia;
        this.canton = canton;
        this.distrito = distrito;
    }

    public String getCodigoElectoral() {
        return codigoElectoral;
    }

    public String getProvincia() {
        return provincia;
    }

    public String getCanton() {
        return canton;
    }

    public String getDistrito() {
        return distrito;
    }
}