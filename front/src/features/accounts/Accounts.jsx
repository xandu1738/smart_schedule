import AddButton from "../../components/AddButton";

const Accounts = () => {
    return (
        <div className="m-8">
            <section className="flex flex-row items-center justify-between">
            <div className="flex flex-col items-start justify-start left-0 w-full">
                <h1 className="font-bold text-3xl">Accounts</h1>
                <p className="text-sm text-gray-500 pt-2">
                Manage user accounts
                </p>
            </div>
            <div>
                <AddButton onClick={() => {}} buttonName={"Add Account"} />
            </div>
            </section>
            <section className="mt-8 grid md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-4">
            </section>
        </div>
    );
};

export default Accounts;