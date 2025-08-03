import AddButton from "../../components/AddButton";
import { getActiveBadge } from "../../components/Badge";
import Table from "../../components/Table";
import { useGetAllAccountsQuery } from "../../helpers/redux/slices/extendedApis/accountsApi";
import { formatText } from "../../helpers/utils";

const Accounts = () => {
    const { data: { data: accounts } = { data: [] }, isLoading, error } = useGetAllAccountsQuery()

    return (
        <div className="m-8 flex flex-col gap-4">
            <section className="flex flex-row items-center justify-between">
                <div className="flex flex-col items-start justify-start left-0 w-full">
                    <h1 className="font-bold text-3xl">Accounts</h1>
                    <p className="text-sm text-gray-500 pt-2">
                        Manage user accounts
                    </p>
                </div>
                <div>
                    <AddButton onClick={() => { }} buttonName={"Add Account"} />
                </div>
            </section>
            <Table
                data={accounts}
                columns={[
                    // { field: "id", header: "ID" },
                    { field: "firstName", header: "First Name"},
                    { field: "lastName", header: "Last Name" },
                    { field: "email", header: "Email" },
                    { field: "username", header: "Username" },
                    { field: "roleCode", header: "Role", sortable: false, body: (data) => formatText(data?.roleCode) },
                    // { field: "createdAt", header: "Created On" },
                    // { field: "lastLoggedInAt", header: "Last Logged In" },
                    { field: "isActive", header: "Active", sortable: false, body: (data) => getActiveBadge(data?.isActive) },
                ]}
                rowActions={[
                    {
                        icon: "Eye",
                        tooltip: "View",
                        onClick: (data) => {
                            console.log(data)
                        }
                    },
                    {
                        icon: "Trash2",
                        tooltip: "Delete",
                        onClick: (data) => {
                            console.log(data)
                        }
                    }
                ]}
                index={accounts?.length}
            />
        </div>
    );
};

export default Accounts;