package pers.cocoade.learning.mysql;

public interface IdCreator<T> {

    T nextId();
}
