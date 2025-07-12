import React from 'react';
import { DataTable } from 'primereact/datatable';
import { Column } from 'primereact/column';
import { Button } from 'primereact/button';
import { Toolbar } from 'primereact/toolbar';
import { InputText } from 'primereact/inputtext';
import { FileUpload } from 'primereact/fileupload';
import { Toast } from 'primereact/toast';
import { confirmDialog } from 'primereact/confirmdialog';
import LucideIcon from './LucideIcon';

export default function Table({
    data = [],
    columns = [],
    selection,
    onSelectionChange,
    paginator = true,
    rows = 10,
    onRowEditComplete,
    editable = false,
    globalFilter = true,
    exportable = true,
    actions = [],
    rowActions = [],
    loading = false,
    dataKey = 'id',
    headerLabel = 'Data',
}) {
    const [filters, setFilters] = React.useState({});
    const [globalFilterValue, setGlobalFilterValue] = React.useState('');

    const handleGlobalFilterChange = (e) => {
        const val = e.target.value;
        setGlobalFilterValue(val);
        setFilters({
            ...filters,
            global: { value: val, matchMode: 'contains' },
        });
    };

    const handleGlobalFilterClear = () => {
        setGlobalFilterValue('');
        setFilters({
            ...filters,
            global: { value: '', matchMode: 'contains' },
        });
    };

    const renderRowActions = (rowData) => (
        <div className="flex gap-2">
            {rowActions.map((action, i) => (
                // <Button
                //     key={i}
                //     icon={action.icon}
                //     className={action.className}
                //     onClick={() => action.onClick(rowData)}
                //     tooltip={action.tooltip}
                // />

                <div className="flex gap-4 items-between">
                    <div className="w-8 h-8 bg-slate-200 text-black rounded-lg flex items-center justify-center pointer" title={action.tooltip}>
                        <LucideIcon
                            name={action.icon}
                            className="text-blue-500"
                            width="15"
                            height="15"
                        />
                    </div>
                </div>
            ))}
        </div>
    );

    const exportCSV = () => {
        if (tableRef.current) tableRef.current.exportCSV();
    };

    const tableRef = React.useRef(null);

    return (
        <>
            <div className="shadow-sm rounded-sm bg-gradient-to-br from-[#fefefe] to-[#f2f5f9] p-6 border border-zinc-200 dark:from-[#1a1c1f] dark:to-[#121416] dark:border-neutral-700">
                <div className="flex justify-between items-center mb-4 border-b border-zinc-200 dark:border-neutral-700 pb-4">
                    {globalFilter && (
                        <div className="min-w-[300px] relative flex items-center border border-gray-300 dark:border-neutral-700 bg-white dark:bg-neutral-900 rounded-sm">
                            <InputText
                                value={globalFilterValue}
                                onChange={handleGlobalFilterChange}
                                placeholder="Search anything..."
                                className="flex-1 pl-5 py-2 rounded-sm border-none outline-none focus:outline-none focus:ring-0 border-gray-300 dark:border-neutral-700 bg-white dark:bg-neutral-900 text-sm"
                            />
                            {globalFilterValue == ''
                                ? <LucideIcon name="Search" className="cursor-pointer absolute right-3 top-1/2 -translate-y-1/2 text-gray-400" />
                                : <LucideIcon name="X" className="cursor-pointer absolute right-3 top-1/2 -translate-y-1/2 text-gray-400" onClick={handleGlobalFilterClear} />}
                        </div>
                    )}
                    {exportable && (
                        <Button
                            icon="pi pi-download"
                            label="Export"
                            className="bg-blue-500 border-none hover:bg-blue-600 text-white px-4 py-2 rounded-sm text-sm"
                            onClick={exportCSV}
                        />
                    )}
                </div>


                <Toast ref={(el) => (window.toast = el)} />
                <DataTable
                    className=""
                    ref={tableRef}
                    value={data}
                    paginator={paginator}
                    rows={rows}
                    stripedRows={true}
                    showGridlines={true}
                    selection={selection}
                    onSelectionChange={onSelectionChange}
                    dataKey={dataKey}
                    filters={filters}
                    globalFilterFields={columns.map((c) => c.field)}
                    filterDisplay="menu"
                    responsiveLayout="scroll"
                    loading={loading}
                // header={renderHeader()}
                // editMode={editable ? 'row' : null}
                // onRowEditComplete={editable ? onRowEditComplete : null}
                >
                    <Column
                        selectionMode="multiple"
                        headerStyle={{ width: '3rem' }}
                        style={{ width: '3rem' }}
                    />
                    {columns.map((col, idx) => (
                        <Column
                            key={idx}
                            field={col.field}
                            header={col.header}
                            sortable={col.sortable}
                            // filter={col.filter ?? true}
                            editor={editable ? col.editor : undefined}
                            body={col.body}
                            style={{
                                ...col.style,
                                textAlign: "start",
                                width: col.style?.width ?? 'auto',
                                
                            }}
                        />
                    ))}
                    {rowActions.length > 0 && (
                        <Column body={renderRowActions} header="Actions" style={{ minWidth: '8rem' }} />
                    )}
                </DataTable>

            </div>
        </>
    );
}
