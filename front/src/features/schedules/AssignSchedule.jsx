import { useNavigate } from "react-router"
import Button from "../../components/Button"
import LucideIcon from "../../components/LucideIcon"
import { useGetAllAccountsQuery } from "../../helpers/redux/slices/extendedApis/accountsApi"
import { useState } from "react"
import { Badge } from "primereact/badge"
import { DragDropContext, Droppable, Draggable } from "@hello-pangea/dnd";
import { Avatar } from "primereact/avatar"
import { getInitials } from "../../helpers/utils"

const AssignSchedule = () => {
    const navigate = useNavigate()
    const [search, setSearch] = useState("")
    const [shiftData, setShiftData] = useState({})
    const { data: { data: accounts } = { data: [] }, isLoading, error } = useGetAllAccountsQuery()
    console.log(accounts)

    const shifts = [
        {
            id: 1,
            name: "Shift 1",
            requiredEmployees: 2,
            assignments: []
        },
        {
            id: 2,
            name: "Shift 2",
            requiredEmployees: 2,
            assignments: []
        },
        {
            id: 3,
            name: "Shift 3",
            requiredEmployees: 2,
            assignments: []
        },
        {
            id: 4,
            name: "Shift 4",
            requiredEmployees: 2,
            assignments: []
        },
        {
            id: 5,
            name: "Shift 5",
            requiredEmployees: 2,
            assignments: []
        }
    ]

    const handleDragEnd = (dragged) => {
        // {
        //     "draggableId": "1",
        //     "type": "DEFAULT",
        //     "source": {
        //         "index": 0,
        //         "droppableId": "unassigned"
        //     },
        //     "reason": "DROP",
        //     "mode": "FLUID",
        //     "destination": {
        //         "droppableId": "1",
        //         "index": 0
        //     },
        //     "combine": null
        // }
        console.log(dragged)
    }

    return (
        <div className="m-8 flex flex-col gap-4">
            <section className="flex flex-row items-center justify-between">
                {/* <Button className={"border border-gray-200 rounded-sm bg-white"} buttonName={"Back"}/> */}
                <button
                    onClick={() => navigate(-1)}
                    className="flex gap-2 border border-gray-200 rounded-sm bg-white p-2 px-4 hover:bg-gray-100 transition-colors cursor-pointer"
                >
                    <LucideIcon name="ChevronLeft" />
                    Back
                </button>
                <div className="flex flex-1 flex-col gap-0 items-start justify-start left-0 w-full">
                    <h1 className="font-bold text-3xl text-center w-full">Assign employees</h1>
                    <p className="text-sm text-gray-500 pt-2 text-center w-full">
                        Some schedule - Marketing
                    </p>
                </div>
                <Button icon={"CheckCheck"} className={"rounded-sm"} buttonName={"Generate schedule"} />
            </section>
            {/* shifts give the cols, shifts are dynamic */}
            <section>
                <div className="flex gap-4 items-center justify-center border-b border-gray-200 rounded-sm p-0 px-4">
                    <LucideIcon
                        name={search ? "X" : "Search"}
                        onClick={() => setSearch("")}
                        className="cursor-pointer text-gray-400 text-sm"
                    />
                    <input
                        type="text"
                        placeholder="Search ..."
                        className="w-full py-0 px-0 border-none border-gray-200 rounded-sm"
                    />
                </div>
            </section>

            <DragDropContext onDragEnd={handleDragEnd}>
                <div className="grid grid-cols-12 gap-2">

                    {/* Unassigned Employees */}
                    <div className="col-span-3 border border-gray-200 rounded-sm p-4 bg-white">
                        <div className="flex gap-2 items-center justify-center p-2">
                            <LucideIcon name="Users" />
                            <h2 className="font-bold text-2xl text-center w-full text-gray-500">
                                Employees
                            </h2>
                            <Badge value="8" severity="success"></Badge>
                        </div>
                        <Droppable droppableId="unassigned">
                            {(provided, snapshot) => (
                                <div
                                    ref={provided.innerRef}
                                    {...provided.droppableProps}
                                    className={`flex flex-col gap-2 min-h-[400px] space-y-2 p-2 rounded-lg transition-colors ${snapshot.isDraggingOver ? 'bg-blue-50' : 'bg-gray-50'
                                        }`}
                                >
                                    {accounts.map((account, index) => (
                                        <Draggable key={account.id} draggableId={`${account.id}`} index={index}>
                                            {(provided, snapshot) => (
                                                <div
                                                    ref={provided.innerRef}
                                                    {...provided.draggableProps}
                                                    {...provided.dragHandleProps}
                                                    className={`p-3 bg-white rounded-lg border shadow-sm cursor-move transition-shadow hover:shadow-md ${snapshot.isDragging ? 'shadow-lg' : ''
                                                        }`}
                                                >
                                                    <div className="flex items-center space-x-3">
                                                        <Avatar name={account.firstName} className="w-8 h-8">
                                                            {/* <AvatarFallback className="bg-gray-200 text-gray-700 text-xs">
                                                                {getInitials(employee.name)}
                                                            </AvatarFallback> */}
                                                            {getInitials(`${account.firstName} ${account.lastName}`)}
                                                        </Avatar>
                                                        <div className="flex-1 min-w-0">
                                                            <p className="font-medium text-sm text-gray-900 truncate">
                                                                {account.firstName} {account.lastName}
                                                            </p>
                                                            <p className="text-xs text-gray-500 truncate">
                                                                {account.roleCode}
                                                            </p>
                                                        </div>
                                                    </div>
                                                </div>
                                            )}
                                        </Draggable>
                                    ))}
                                    {provided.placeholder}
                                </div>
                            )}
                        </Droppable>
                    </div>

                    {/* Shift Columns */}
                    <div className="col-span-9 grid grid-cols-12 gap-2 overflow-x-auto">
                        {
                            shifts.map((shift) => (<>
                                <div className="col-span-4 rounded-sm p-4 bg-white">
                                    <div className="flex gap-2 items-center justify-center p-2">
                                        <LucideIcon name="Users" />
                                        <h2 className="font-bold text-2xl text-center w-full text-gray-500">
                                            {shift.name}
                                        </h2>
                                        <Badge value={shift.requiredEmployees} severity="success"></Badge>
                                    </div>

                                    <Droppable droppableId={`${shift.id}`}>
                                        {(provided, snapshot) => (
                                            <div
                                                ref={provided.innerRef}
                                                {...provided.droppableProps}
                                                className={`flex flex-col gap-2 min-h-[400px] space-y-2 p-2 rounded-lg transition-colors ${snapshot.isDraggingOver ? 'bg-blue-50' : 'bg-gray-50'
                                                    }`}
                                            >
                                                {shiftData?.[shift.id]?.map((account, index) => (
                                                    <Draggable key={account.id} draggableId={`${account.id}`} index={index}>
                                                        {(provided, snapshot) => (
                                                            <div
                                                                ref={provided.innerRef}
                                                                {...provided.draggableProps}
                                                                {...provided.dragHandleProps}
                                                                className={`p-3 bg-white rounded-lg border shadow-sm cursor-move transition-shadow hover:shadow-md ${snapshot.isDragging ? 'shadow-lg' : ''
                                                                    }`}
                                                            >
                                                                <div className="flex items-center space-x-3">
                                                                    <Avatar name={account.firstName} className="w-8 h-8">
                                                                        {getInitials(`${account.firstName} ${account.lastName}`)}
                                                                    </Avatar>
                                                                    <div className="flex-1 min-w-0">
                                                                        <p className="font-medium text-sm text-gray-900 truncate">
                                                                            {account.firstName} {account.lastName}
                                                                        </p>
                                                                        <p className="text-xs text-gray-500 truncate">
                                                                            {account.roleCode}
                                                                        </p>
                                                                    </div>
                                                                </div>
                                                            </div>
                                                        )}
                                                    </Draggable>
                                                ))}
                                                {provided.placeholder}
                                            </div>
                                        )}
                                    </Droppable>
                                </div>

                            </>))
                        }
                    </div>
                </div>
            </DragDropContext>
        </div>
    )
}

export default AssignSchedule