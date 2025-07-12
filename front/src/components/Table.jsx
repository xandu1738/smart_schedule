
import React, { useState, useEffect } from 'react';
import { DataTable } from 'primereact/datatable';
import { Column } from 'primereact/column';
import { ProductService } from './service/ProductService';

export default function Table({data, column, index}) {

    return (
        <div className="card">
            <DataTable value={data} stripedRows tableStyle={{ minWidth: '50rem' }}>
                {column.map((col, index) => (
                    <Column key={index} field={col.field} header={col.header} />
                ))}
            </DataTable>
        </div>
    );
}
        