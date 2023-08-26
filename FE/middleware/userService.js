export async function updateUser(axios, firstName, lastName, email)
{
    return await axios.put(`api/Gateway/User`,
        {
            firstName,
            lastName,
            email
        });
}