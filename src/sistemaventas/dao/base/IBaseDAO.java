/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sistemaventas.dao.base;
import java.util.List;
import java.util.Optional;

public interface IBaseDAO<T, ID> {
    T create(T entity) throws Exception;
    Optional<T> findById(ID id) throws Exception;
    List<T> findAll() throws Exception;
    T update(T entity) throws Exception;
    boolean delete(ID id) throws Exception;
    long count() throws Exception;
}