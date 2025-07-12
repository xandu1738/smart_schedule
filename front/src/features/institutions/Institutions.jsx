import React from "react";
import AddButton from "../../components/AddButton";
import InstitutionCard from "../../components/InstitutionCard";
import AddInstitutionDialog from "./AddInstitutionDialog";

const Institutions = (props) => {
  const [showDialog, setShowDialog] = React.useState(false);
  const [institutions, setInstitutions] = React.useState([]);
  const [refetch, setRefetch] = React.useState(0);

  return (
    <>
      <div className="m-8">
        <section className="flex flex-row items-center justify-between">
          <div className="flex flex-col items-start justify-start left-0 w-full">
            <h1 className="font-bold text-3xl">Institutions</h1>
            <p className="text-sm text-gray-500 pt-2">
              Manage your Institutions
            </p>
          </div>
          <div>
            <AddButton
              onClick={() => setShowDialog(true)}
              buttonName={"Add Institution"}
            />
          </div>
        </section>
        <section className="mt-8 grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
          {institutions.map((institution) => (
			<InstitutionCard
			  key={institution.id}
			  institution={institution.name}
			  onClick={() => {}}
			  description={institution.description}
			  managerName={institution.managerName}
			/>
		  ))}
        </section>
      </div>
      {showDialog && (
        <AddInstitutionDialog setShowDialog={setShowDialog} setRefetch={setRefetch} />
      )}
    </>
  );
};

export default Institutions;
