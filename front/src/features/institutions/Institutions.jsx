import React from "react";
import AddButton from "../../components/AddButton";
import AddInstitutionDialog from "./AddInstitutionDialog";
import Table from "../../components/Table";
import { useGetAllInstitutionsQuery } from "../../helpers/redux/slices/extendedApis/institutionsApi";
import { formatDate, formatText } from "../../helpers/utils";
import { getActiveBadge, getPlainBadge } from "../../components/Badge";

// {
//     "id": 1,
//     "name": "ROTARY LIMITED",
//     "code": "ROTARY_LIMITED",
//     "description": "digitaliizing commence",
//     "ownerName": "paul peter",
//     "location": "mukono",
//     "regNo": "994353",
//     "yearEstablished": "2025",
//     "institutionType": "formal",
//     "createdAt": "2025-07-12T14:31:45.971040Z",
//     "createdBy": 1,
//     "active": true
// }

const data = (props) => {
    const [showDialog, setShowDialog] = React.useState(false);
    const { data, isLoading, error } = useGetAllInstitutionsQuery()

    return (
        <>
            <div className="m-8 flex flex-col gap-4">
                <section className="flex flex-row items-center justify-between">
                    <div className="flex flex-col items-start justify-start left-0 w-full">
                        <h1 className="font-bold text-3xl">Institutions</h1>
                        <p className="text-sm text-gray-500 pt-2">
                            Manage your institutions
                        </p>
                    </div>
                    <div>
                        <AddButton
                            onClick={() => setShowDialog(true)}
                            buttonName={"Add Institution"}
                        />
                    </div>
                </section>
                <Table
                    data={data?.data}
                    columns={[
                        { field: "name", header: "Name", style: { fontWeight: "bold" } },
                        // { field: "code", header: "Code" },
                        // { field: "description", header: "Description" },
                        { field: "ownerName", header: "Owner Name" },
                        { field: "location", header: "Location" },
                        { field: "regNo", header: "Reg No" },
                        // { field: "yearEstablished", header: "Year Established" },
                        { field: "institutionType", header: "Institution Type", body: (data) => getPlainBadge(formatText(data?.institutionType)) },
                        { field: "createdAt", header: "Created On", body: (data) => formatDate(data?.createdAt), sortable: true },
                        // { field: "createdBy", header: "Created By" },
                        { field: "active", header: "Active", body: (data) => getActiveBadge(data?.active) },
                    ]}
                    rowActions={[
                        {
                            icon: "Eye",
                            tooltip: "View",
                            onClick: (data) => {
                                console.log(data)
                            }
                        },
                    ]}
                    rows={7}
                    index={data?.length}
                />
            </div>
            {showDialog && (
                <AddInstitutionDialog setShowDialog={setShowDialog} />
            )}
        </>
    );
};

export default data;
