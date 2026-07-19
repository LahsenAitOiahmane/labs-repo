<?php
/**
 * IRepository — contrat générique pour les opérations CRUD.
 *
 * @template T
 */
interface IRepository
{
    public function create($entity): bool;
    public function update($entity): bool;
    public function delete($entity): bool;
    public function findById(int $id);
    public function findAll(): array;
}
