package com.leonardo.arkansasproject.database;

import io.smallrye.mutiny.Uni;

public interface Service<O, I> {

    Uni<O> create(O object);

    Uni<O> read(I id);

    Uni<O> update(O object);

    Uni<Void> delete(O object);

    Uni<O> deleteById(I id);

    Uni<Boolean> exists(I id);

}
